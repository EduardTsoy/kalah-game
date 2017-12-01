CREATE TABLE kalah_game (
  id                                   INT8    NOT NULL PRIMARY KEY AUTO_INCREMENT,
  version                              INT4    NOT NULL,
  number_of_small_pits_for_each_player INTEGER NOT NULL,
  initial_number_of_stones_in_each_pit INTEGER NOT NULL,
  first_store                          INTEGER NOT NULL,
  first_pits                           VARCHAR,
  second_store                         INTEGER NOT NULL,
  second_pits                          VARCHAR,
  active_player                        INTEGER NOT NULL,
  first_message                        VARCHAR,
  second_message                       VARCHAR,
  event_log                            VARCHAR,
  finish_message                       VARCHAR,
  game_over_message                    VARCHAR
);
