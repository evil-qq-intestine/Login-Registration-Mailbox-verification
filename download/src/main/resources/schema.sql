CREATE TABLE IF NOT EXISTS user (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    user_name TEXT NOT NULL UNIQUE,
                                    password TEXT NOT NULL,
                                    email TEXT NOT NULL UNIQUE,
                                    enabled INTEGER DEFAULT 0
);

