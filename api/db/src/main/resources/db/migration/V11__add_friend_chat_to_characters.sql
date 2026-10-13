-- The player's own chat-channel setup (name and enter/talk/kick ranks), stored as JSON.
ALTER TABLE characters
ADD COLUMN friend_chat TEXT;

-- Chat-channel ranks given to names on the friend list, stored as a JSON object.
ALTER TABLE characters
ADD COLUMN friend_chat_ranks TEXT;
