# Getting Started

### Как запустить приложение
* Gradle task -> dockerBuildImage соберет образ приложения otus-app
* Gradle task -> composeUp выполнит запуск docker compose

![img.png](img.png)

## Ключевые изменения
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

### Домашнее задание Кеширование
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
