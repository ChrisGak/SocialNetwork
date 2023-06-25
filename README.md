# Заготовка для социальной сети

## Домашнее задание №1 Проблемы высоких нагрузок

### Как запустить приложение
* Gradle task -> dockerBuildImage соберет образ приложения otus-app
* Gradle task -> composeUp выполнит запуск docker compose

![img.png](img.png)

## Домашнее задание №2 Индексы (часть 2)
### (опционально) генерация тестовых данных;
#### Использован набор тестовых данных для проведения нагрузочного тестирования
* Файл people.csv предварительно был отредактирован, чтобы каждое значение для колонки было разделено по отдельным колонкам (через символ ";")
* С помощью Liquibase функционала loadData https://docs.liquibase.com/change-types/load-data.html загружается список пользователей из файла people.csv 
### работа с индексами
* Запрос Find which tables use INNODB with MySQL
```
SELECT table_schema, table_name, engine
FROM INFORMATION_SCHEMA.TABLES
WHERE engine = 'innodb';
```
### нагрузочное тестирование
Проведено в jMeter

## Домашнее задание №3 Репликация (часть 3)
За основу взяты скрипты из https://github.com/OtusTeam/highload/blob/master/lessons/02/05/live/guide.md
# Репликация в PostgreSQL
## Физическая репликация
docker exec -it master_db su - postgres -c psql
1. Создаем сеть в docker-compose, запоминаем адрес
    ```shell
    networks:
      otus_pgnetwork:
   
    docker network ls
    docker network create otus_pgnetwork
    docker network inspect otus_pgnetwork | grep Subnet 
   # Запомнить маску сети 172.19.0.0/16
    ```

2. Поднимаем мастер в docker-compose
```
   docker run -dit -v "$PWD/db_volume/master_db/:/var/lib/postgresql/data" -e POSTGRES_PASSWORD=postgres -p "5432:5432" --restart=unless-stopped --network=otus_pgnetwork --name=master_db postgres
   ```
3. Меняем postgresql.conf на мастере
    ```conf
    ssl = off#
    wal_level = replica
    max_wal_senders = 4 # expected slave num
    ```

4. Подключаемся к мастеру и создаем пользователя для репликации
Также добавили скрипт в db_volume/init.sql, который делает то же самое
    ```shell
    docker exec -it master_db su - postgres -c psql
    create role replicator with login replication password 'pass'; 
    select * from pg_roles where rolname = 'replicator';
    exit
    ```

5. Добавляем запись в `master_db/pg_hba.conf` с `subnet` с первого шага
    ```
    host    replication     replicator       __SUBNET__          md5
    host    replication     replicator       172.19.0.0/16       md5
    ```

6. Перезапустим мастер
    ```shell
    docker restart master_db
    ```

7. Сделаем бэкап для реплик
    ```shell
    docker exec -it master_db bash
    mkdir /replica_backup
    pg_basebackup -h master_db -D /replica_backup -U replicator -v -P --wal-method=stream
    exit
    ```

8. Копируем директорию себе в реплику replica_db_1
    ```shell
    docker cp master_db:/replica_backup db_volume/replica_db_1
    ```

9. Создадим файл, чтобы реплика узнала, что она реплика
    ```shell
    Windows:
    C:\Docker\volumes\replica_db_1\standby.signal
    Mac:
    touch db_volume/replica_db_1/standby.signal
    ```

10. Меняем `postgresql.conf` на реплике `replica_db_1`
    ```conf
    primary_conninfo = 'host=master_db port=5432 user=replicator password=pass application_name=replica_db_1'
    ```

11. Запускаем реплику `replica_db_1` - указано в docker-compose.yml
    ```shell
    docker run -dit -v "$PWD/db_volume/replica_db_1/:/var/lib/postgresql/data" -e POSTGRES_PASSWORD=pass -p "15432:5432" --network=otus_pgnetwork --restart=unless-stopped --name=replica_db_1 postgres
    ```
    
12. Запустим вторую реплику `replica_db_2`
  - скопируем бэкап
      ```shell
      docker cp master_db:/replica_backup db_volume/replica_db_2/
      ```

  - изменим настройки `replica_db_2/postgresql.conf`
      ```conf
      primary_conninfo = 'host=master_db port=5432 user=replicator password=pass application_name=replica_db_2'
      ```

  - дадим знать что это реплика
      ```shell
     Windows:
     C:\Docker\volumes\replica_db_2\standby.signal
     Mac:
      touch db_volume/replica_db_2/standby.signal
      ```

  - запустим реплику `replica_db_2`  - указано в docker-compose.yml
    (Перезапустим replica_db_2)
      ```shell
        docker restart replica_db_2
    
        docker run -dit -v "$PWD/db_volume/replica_db_2/:/var/lib/postgresql/data" -e POSTGRES_PASSWORD=pass -p "25432:5432" --network=otus_pgnetwork --restart=unless-stopped --name=replica_db_2 postgres
   
        ```

14. Убеждаемся что обе реплики работают в асинхронном режиме на `master_db`
    ```shell
    docker exec -it master_db su - postgres -c psql
    select application_name, sync_state from pg_stat_replication;
    exit;
    ```
### Настроить кворумную синхронную репликацию.
1. Включаем синхронную репликацию на `master_db`
    - меняем файл `db_volume/master_db/postgresql.conf`
        ```conf
        synchronous_commit = on
        synchronous_standby_names = 'FIRST 1 (replica_db_1, replica_db_2)'
        ```

    - перечитываем конфиг
        ```shell
        docker exec -it master_db su - postgres -c psql
        select pg_reload_conf();
        exit;
        ```

2. Убеждаемся, что реплики стала синхронными
    ```shell
    docker exec -it master_db su - postgres -c psql
    select application_name, sync_state from pg_stat_replication;
    exit;
    ```

3. Укладываем репилку `replica_db_2` и проверяем работу `master_db` и `replica_db_1`
    ```shell
    docker stop replica_db_2
    docker exec -it master_db su - postgres -c psql
    select application_name, sync_state from pg_stat_replication;
    insert into test(id) values(2);
    select * from test;
    exit;
    docker exec -it replica_db_1 su - postgres -c psql
    select * from test;
    exit;
    ```
4. Создадим тестовую таблицу на `master_db` и проверим репликацию
    ```shell
    docker exec -it master_db su - postgres -c psql
    create table test(id bigint primary key not null);
    insert into test(id) values(2);
    select * from test;
    exit;

5. Возвращаем вторую реплику `replica_db_2`
    ```shell
    docker start replica_db_2
    ```
6. Убиваем мастер `master_db`
    ```shell
    docker stop master_db
    ```
7. Запромоутим реплику `replica_db_1`
    ```shell
    docker exec -it replica_db_1 su - postgres -c psql
    select pg_promote();
    exit;
    ```
8. Настраиваем репликацию на `replica_db_1` (`replica_db_1/postgresql.conf`)
    - изменяем конфиг
        ```conf
        synchronous_commit = on
        synchronous_standby_names = 'ANY 1 (master_db, replica_db_2)'
        ```
    - перечитываем конфиг
        ```shell
        docker exec -it replica_db_1 su - postgres -c psql
        select pg_reload_conf();
        exit;
        ```

9. Подключим вторую реплику `replica_db_2` к новому мастеру `replica_db_1`
    - изменяем конфиг `replica_db_2/postgresql.conf`
        ```conf
        primary_conninfo = 'host=replica_db_1 port=5432 user=replicator password=pass application_name=replica_db_2'
        ```
    - перечитываем конфиг
        ```shell
        docker exec -it replica_db_2 su - postgres -c psql
        select pg_reload_conf();
        exit;
        ```
10. Проверяем что к новому мастеру `replica_db_1` подключена реплика и она работает
    ```shell
    docker exec -it replica_db_1 su - postgres -c psql
    select application_name, sync_state from pg_stat_replication;
    exit;
    ```
11. Восстановим старый мастер `master_db` как реплику
    1. Помечаем как реплику
        ```shell
        touch db_volume/master_db/standby.signal
        ```
    2. Изменяем конфиг `master_db/postgresql.conf`
        ```conf
        primary_conninfo = 'host=replica_db_1 port=5432 user=replicator password=pass application_name=master_db'
        ```
    3. Запустим `master_db`
       ```shell
        docker start master_db
        ```
    4. Убедимся что `master_db` подключился как реплика к `replica_db_1`
        ```shell
        docker exec -it replica_db_1 su - postgres -c psql
        select application_name, sync_state from pg_stat_replication;
        exit;
        ```

## Домашнее задание №4 Кеширование
* В /login API добавлена генерация токена - в куки X-Auth
Требуется ручная очистка куки 
![img_1.png](img_1.png)
* Добавлены апи на добавление и удаление друзей
```
PUT {{baseUrl}}/friend/add/{id}
PUT {{baseUrl}}/friend/delete/{id}
```
* Добавлены апи на создание и удаление постов
```
POST {{baseUrl}}/post/create
PUT {{baseUrl}}/post/delete?{id}
```
* Добавлено апи на получение ленты новостей друзей
```
GET {{baseUrl}}/post/feed?offset=0&limit=50
```
Формирование кеша основано на [caching in a Spring application](https://www.baeldung.com/spring-cache-tutorial)
Кэш - объект ConcurrentMapCacheManager с ключом "friendPosts"
Представляет собой map:
key - userId, value - list<Post>
* Инвалидация кеша происходит в следующих случаях
  * 1 Добавление или удаление друзей у текущего пользователя - инвалидируется значение в кеше для userId
  * 2 Добавление или удаление постов у текущего пользователя - инвалидируется значение в кеше для пользователей, являющихся друзьями текушего userId

## Домашнее задание #5 Масштабируемая подсистема диалогов
