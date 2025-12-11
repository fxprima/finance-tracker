CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     password TEXT NOT NULL,
                                     first_name TEXT,
                                     last_name TEXT,
                                     email TEXT,
                                     phone TEXT
);

CREATE TABLE IF NOT EXISTS categories (
                                          id BIGSERIAL PRIMARY KEY,
                                          user_id BIGINT,
                                          description TEXT NOT NULL,
                                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                          CONSTRAINT fk_categories_user
                                          FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS sub_categories (
                                              id BIGSERIAL PRIMARY KEY,
                                              category_id BIGINT,
                                              description TEXT NOT NULL,
                                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                              CONSTRAINT fk_sub_categories_category
                                              FOREIGN KEY (category_id) REFERENCES categories(id)
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGSERIAL PRIMARY KEY,
                                            user_id BIGINT,
                                            category_id BIGINT,
                                            sub_category_id BIGINT,

                                            type TEXT NOT NULL,
                                            description TEXT,
                                            date TIMESTAMP NOT NULL,
                                            amount INTEGER NOT NULL,
                                            currency TEXT NOT NULL,
                                            note TEXT,
                                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                            CONSTRAINT fk_transactions_user
                                            FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_transactions_category
    FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_transactions_subcategory
    FOREIGN KEY (sub_category_id) REFERENCES sub_categories(id)
    );
