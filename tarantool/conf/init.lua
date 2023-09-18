-- Tarantool configuration
-- Create dialogue space

uuid = require('uuid')

print('Hello, it is Tarantool init.lua script!')

--Configure database
box.cfg {
    listen = 3301
}
box.once("bootstrap", function()
    print('Create space on Tarantool bootstrap')
    -- Create a space --
    box.schema.space.create('dialogue', {if_not_exists = true})
    -- Specify field names and types --
    box.space.dialogue:format({
        { name = "id", type = "uuid" },
        { name = "from_user_id", type = "string" },
        { name = "to_user_id", type = "string" },
        { name = "text", type = "string" },
    })
    -- Create a primary index --
    box.space.dialogue:create_index("primary", { parts = { { "id" } }}, {if_not_exists = true})
    -- Create a non-unique secondary index --
    box.space.dialogue:create_index("from_user_id_idx", { parts = { { "from_user_id" } }, unique = false}, {if_not_exists = true})
    box.space.dialogue:create_index("to_user_id_idx", { parts = { { "to_user_id" } }, unique = false}, {if_not_exists = true})
end)

function start()
    print('Create space on Tarantool bootstrap')
    -- Create a space --
    box.schema.space.create('dialogue')
    -- Specify field names and types --
    box.space.dialogue:format({
        { name = "id", type = "uuid" },
        { name = "from_user_id", type = "string" },
        { name = "to_user_id", type = "string" },
        { name = "text", type = "string" },
    })
    -- Create a primary index --
    box.space.dialogue:create_index("primary", { parts = { { "id" } }})
    -- Create a non-unique secondary index --
    box.space.dialogue:create_index("from_user_id_idx", { parts = { { "from_user_id" } }, unique = false})
    box.space.dialogue:create_index("to_user_id_idx", { parts = { { "to_user_id" } }, unique = false})
end

function saveMessage(fromUserId, toUserId, text)
    print('Save dialogue message')
    box.space.dialogue:insert({ uuid.new(), fromUserId, toUserId, text })
    -- return { id = uuid.new(), from_user_id = fromUserId, to_user_id = toUserId, text = 'text' }
end

function find_all_messages_by_users(currentUserId, anotherUserId)
    print('Start find_all_messages_by_users')
    local result = {}
    -- Search all
    -- result = box.space.dialogue:select({})
    print('Search messages from user: ' .. currentUserId)
    for _, tuple in box.space.dialogue.index.from_user_id_idx:pairs({ currentUserId }, { iterator = 'EQ' }) do
        print(tuple)
        if tuple[3] == anotherUserId then
            table.insert(result, tuple)
        end
    end
    print('Search messages from user: ' .. anotherUserId)
    for _, tuple in box.space.dialogue.index.from_user_id_idx:pairs({ anotherUserId }, { iterator = 'EQ' }) do
        print(tuple)
        if tuple[3] == currentUserId then
            table.insert(result, tuple)
        end
    end
    return result
end

--return {
--    start = start;
--}