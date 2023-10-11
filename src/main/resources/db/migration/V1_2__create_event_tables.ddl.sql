CREATE TABLE events (
    id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    place VARCHAR(255) NOT NULL,
    creator_id INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE TABLE events_responses (
    id INT NOT NULL,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    status VARCHAR(11),
    PRIMARY KEY (id),
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
