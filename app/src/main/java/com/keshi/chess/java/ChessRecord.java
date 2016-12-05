package com.keshi.chess.java;

import android.graphics.Point;

// This class is used to save records.
public class ChessRecord{
    private Point from;
    private Point to;
    private byte choosePiece,toPiece;
    private int value;

    public ChessRecord(Point from,Point to,byte choosePiece,byte toPiece){
        this.from=from;
        this.to=to;
        this.choosePiece=choosePiece;
        this.toPiece=toPiece;
    }

    public Point getFrom() {
        return from;
    }

    public Point getTo() {
        return to;
    }

    public byte getChoosePiece() {
        return choosePiece;
    }

    public byte getToPiece() {
        return toPiece;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}