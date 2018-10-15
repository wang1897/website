package com.aethercoder.core.entity.json;

import com.aethercoder.core.entity.guess.GuessNumberGame;
import com.aethercoder.core.entity.guess.GuessRecord;

public class GameJson {

    private String type;

    private GuessRecord guessRecord;

    private GuessNumberGame guessNumberGame;

    private Long date;


    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GuessRecord getGuessRecord() {
        return guessRecord;
    }

    public void setGuessRecord(GuessRecord guessRecord) {
        this.guessRecord = guessRecord;
    }

    public GuessNumberGame getGuessNumberGame() {
        return guessNumberGame;
    }

    public void setGuessNumberGame(GuessNumberGame guessNumberGame) {
        this.guessNumberGame = guessNumberGame;
    }
}
