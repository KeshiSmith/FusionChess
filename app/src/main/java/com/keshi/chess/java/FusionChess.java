package com.keshi.chess.java;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static java.lang.Math.abs;

public class FusionChess {
    // Set static member about piece.
    public static final byte BLANK=0;       // 空白
    public static final byte KING=1;        // 将
    public static final byte ADVISER =2;    // 士
    public static final byte ELEPHANT=3;    // 象
    public static final byte HORSE=4;       // 马
    public static final byte CHARIOT=5;     // 车
    public static final byte CANNON=6;      // 炮
    public static final byte PAWN=7;        // 卒
    public static final byte HORSE_HORSE=8;     // 双马
    public static final byte HORSE_CHARIOT=9;   // 马车
    public static final byte HORSE_CANNON=10;   // 马炮
    public static final byte HORSE_PAWN=11;     // 马卒
    public static final byte CHARIOT_CHARIOT=12;// 双车
    public static final byte CHARIOT_PAWN=13;   // 车卒
    public static final byte CANNON_CHARIOT=14; // 炮车
    public static final byte CANNON_CANNON=15;  // 双炮
    public static final byte CANNON_PAWN=16;    // 炮卒
    public static final byte PAWN_PAWN=17;      // 双卒
    // Set static default chessboard.
    public static final byte defaultBoard[][]= {
            {CHARIOT,    HORSE,      ELEPHANT,   ADVISER,    KING},
            {BLANK,      CANNON,     BLANK,      BLANK,      BLANK},
            {PAWN,       BLANK,      PAWN,       BLANK,      PAWN}};
    // Set static fusion-pieces array.(x,y)->HORSE,CHARIOT,CANNON,PAWN
    public static final byte fusionPieces[][]={
            {HORSE_HORSE,       HORSE_CHARIOT,      HORSE_CANNON,     HORSE_PAWN},
            {HORSE_CHARIOT,     CHARIOT_CHARIOT,    CANNON_CHARIOT,   CHARIOT_PAWN},
            {HORSE_CANNON,      CANNON_CHARIOT,     CANNON_CANNON,    CANNON_PAWN},
            {HORSE_PAWN,        CHARIOT_PAWN,       CANNON_PAWN,      PAWN_PAWN}};
    // Set static divide-pieces array.(x=x-8:HORSE_CHARIOT...)
    public static final byte dividePieces[][]={
            {HORSE,HORSE},      // x=0=HORSE_HORSE
            {HORSE,CHARIOT},    // x=1=HORSE_CHARIOT ...
            {HORSE,CANNON},{HORSE,PAWN},{CHARIOT,CHARIOT},
            {CHARIOT,PAWN},{CANNON,CHARIOT},{CANNON,CANNON},{CANNON,PAWN},{PAWN,PAWN}};
    // The static array save value of every piece.
    public static final int value[][][]={{
            {0, 0, 0, 8888, 8888, 8888, 0, 0, 0},//King
            {0, 0, 0, 8888, 8888, 8888, 0, 0, 0},
            {0, 0, 0, 8888, 8888, 8888, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},

            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 8888, 8888, 8888, 0, 0, 0},
            {0, 0, 0, 8888, 8888, 8888, 0, 0, 0},
            {0, 0, 0, 8888, 8888, 8888, 0, 0, 0}},

            {{0, 0, 0,20, 0,20, 0, 0, 0},//ADVISER
            {0, 0, 0, 0,23, 0, 0, 0, 0},
            {0, 0, 0,20, 0,20, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},

            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0,20, 0,20, 0, 0, 0},
            {0, 0, 0, 0,23, 0, 0, 0, 0},
            {0, 0, 0,20, 0,20, 0, 0, 0}},

            {{0, 0,20, 0, 0, 0,20, 0, 0},//ELEPHANT
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0,23, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0,20, 0, 0, 0,20, 0, 0},

            {0, 0,20, 0, 0, 0,20, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {18,0, 0, 0,23, 0, 0, 0,18},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0,20, 0, 0, 0,20, 0, 0}},


            {{90, 90, 90, 96, 90, 96, 90, 90, 90},//HORSE
            {90, 96,103, 97, 94, 97,103, 96, 90},
            {92, 98, 99,103, 99,103, 99, 98, 92},
            {93,108,100,107,100,107,100,108, 93},
            {90,100, 99,103,104,103, 99,100, 90},

            {90, 98,101,102,103,102,101, 98, 90},
            {92, 94, 98, 95, 98, 95, 98, 94, 92},
            {93, 92, 94, 95, 92, 95, 94, 92, 93},
            {85, 90, 92, 93, 78, 93, 92, 90, 85},
            {88, 85, 90, 88, 90, 88, 90, 85, 88}},

            {{206, 208, 207, 213, 214, 213, 207, 208, 206},//CHARIOT
            {206, 212, 209, 216, 233, 216, 209, 212, 206},
            {206, 208, 207, 214, 216, 214, 207, 208, 206},
            {206, 213, 213, 216, 216, 216, 213, 213, 206},
            {208, 211, 211, 214, 215, 214, 211, 211, 208},

            {208, 212, 212, 214, 215, 214, 212, 212, 208},
            {204, 209, 204, 212, 214, 212, 204, 209, 204},
            {198, 208, 204, 212, 212, 212, 204, 208, 198},
            {200, 208, 206, 212, 200, 212, 206, 208, 200},
            {194, 206, 204, 212, 200, 212, 204, 206, 194}},

            {{100, 100,  96, 91,  90, 91,  96, 100, 100},//CANNON
            { 98,  98,  96, 92,  89, 92,  96,  98,  98},
            { 97,  97,  96, 91,  92, 91,  96,  97,  97},
            { 96,  99,  99, 98, 100, 98,  99,  99,  96},
            { 96,  96,  96, 96, 100, 96,  96,  96,  96},

            { 95,  96,  99, 96, 100, 96,  99,  96,  95},
            { 96,  96,  96, 96,  96, 96,  96,  96,  96},
            { 97,  96, 100, 99, 101, 99, 100,  96,  97},
            { 96,  97,  98, 98,  98, 98,  98,  97,  96},
            { 96,  96,  97, 99,  99, 99,  97,  96,  96}},

            {{ 9,  9,  9, 11, 13, 11,  9,  9,  9},//PAWN
            {19, 24, 34, 42, 44, 42, 34, 24, 19},
            {19, 24, 32, 37, 37, 37, 32, 24, 19},
            {19, 23, 27, 29, 30, 29, 27, 23, 19},
            {14, 18, 20, 27, 29, 27, 20, 18, 14},

            { 7,  0, 13,  0, 16,  0, 13,  0,  7},
            { 7,  0,  7,  0, 15,  0,  7,  0,  7},
            { 0,  0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0,  0}}};
    // ChessBoard is used to save values about chess information.
    private byte chessBoard[][]=new byte[10][9];
    // The Stack is for Saving data of records.
    Stack<ChessRecord> chessRecordsStack= new Stack();
    // Initialize class FusionChess.
    public FusionChess(){
        initChessBoard();
    }

    /**
     * This function is used to get pieces on chessboard.
     * @param x The location.x of piece
     * @param y The location.y of piece
     */
    public byte getChessBoard(int x,int y) {
        return chessBoard[x][y];
    }

    /**
     * This function is used to set pieces on chessboard.
     * @param x The location.x of piece
     * @param y The location.y of piece
     * @param piece The type of piece.
     */
    public void setChessBoard(int x,int y,byte piece) {
        chessBoard[x][y] = piece;
    }

    /**
     * This function is used to initialize Chessboard.
     */
    public void initChessBoard(){
        for(int i=0;i<9;i++){
            setChessBoard(0,i,defaultBoard[0][abs(abs(4-i)-4)]);
            setChessBoard(1,i,BLANK);
            setChessBoard(2,i,defaultBoard[1][abs(abs(4-i)-4)]);
            setChessBoard(3,i,defaultBoard[2][abs(abs(4-i)-4)]);
            setChessBoard(4,i,BLANK);
            setChessBoard(5,i,BLANK);
            setChessBoard(6,i,(byte)-defaultBoard[2][abs(abs(4-i)-4)]);
            setChessBoard(7,i,(byte)-defaultBoard[1][abs(abs(4-i)-4)]);
            setChessBoard(8,i,BLANK);
            setChessBoard(9,i,(byte)-defaultBoard[0][abs(abs(4-i)-4)]);
        }
        chessRecordsStack.clear(); // Clear stack.
    }

    /**
     * This function is to judge the location of piece which you choose is if legal.
     * @param location The location you want to judge.
     * @param rangeU Range_Up
     * @param rangeD Range_Down
     * @param rangeL Range_Left
     * @param rangeR Range_Right
     * @return true=legal, false=illegal
     */
    public boolean locationIsLegal(Point location,int rangeU,int rangeD,int rangeL,int rangeR){
        //判断棋子是否越出范围
        if(location.x<=rangeU&&location.x>=rangeD&&location.y>=rangeL&&location.y<=rangeR)
            return true;
        return false;
    }

    /**
     * This function is to get the list that you can move.
     * @param location The location where you choose that piece.
     * @param choosePiece The piece you want to move.
     * @return The list of moving location.
     */
    public Set<Point> getMoveList(Point location, byte choosePiece){
        Set<Point> list=new HashSet();
        byte absChoosePiece=(byte)(abs(choosePiece));
        boolean typeIsBlue=(choosePiece<0);
        switch (absChoosePiece){
            case BLANK:
                break;
            case KING: {
                Point[] moveList = {new Point(1, 0), new Point(-1, 0), new Point(0, -1), new Point(0, 1)};
                int rangeU=2, rangeD=0;
                if (typeIsBlue) {
                    rangeU += 7;
                    rangeD += 7;
                }
                for (Point i : moveList) {
                    Point move = new Point(location);
                    move.x += i.x;
                    move.y += i.y;
                    if(!locationIsLegal(move, rangeU, rangeD, 3, 5))
                        continue;
                    byte toPiece=getChessBoard(move.x, move.y);
                    if (choosePiece * toPiece <= 0)
                        list.add(move);
                }
                // Judge if two King are face to face.
                Point i=typeIsBlue? moveList[1]:moveList[0];
                Point move = new Point(location);
                while(true){
                    move.x += i.x;
                    move.y += i.y;
                    if(!locationIsLegal(move, 9, 0, 3, 5))
                        break;
                    byte toPiece=getChessBoard(move.x, move.y);
                    if(toPiece!=BLANK){
                        if(abs(toPiece)==KING)
                            list.add(move);
                        break;
                    }
                }
                break;
            }
            case ADVISER: {
                Point[] moveList = {new Point(1, 1), new Point(-1, 1), new Point(-1, -1), new Point(1, -1)};
                int rangeU=2, rangeD=0;
                if (typeIsBlue) {
                    rangeU += 7;
                    rangeD += 7;
                }
                for (Point i : moveList) {
                    Point move = new Point(location);
                    move.x += i.x;
                    move.y += i.y;
                    if(!locationIsLegal(move, rangeU, rangeD, 3, 5))
                        continue;
                    byte toPiece=getChessBoard(move.x, move.y);
                    if (choosePiece * toPiece <= 0)
                        list.add(move);
                }
                break;
            }
            case ELEPHANT: {
                Point[] moveList = {new Point(2, 2), new Point(-2, 2), new Point(-2, -2), new Point(2, -2)};
                int rangeU=4, rangeD=0;
                if (typeIsBlue) {
                    rangeU += 5;
                    rangeD += 5;
                }
                for (Point i : moveList) {
                    Point move = new Point(location);
                    move.x += i.x;
                    move.y += i.y;
                    if(!locationIsLegal(move, rangeU, rangeD, 0, 8))
                        continue;
                    if(getChessBoard(location.x+i.x/2,location.y+i.y/2)!=BLANK)
                        continue;   // Elephant eye.
                    byte toPiece=getChessBoard(move.x, move.y);
                    if (choosePiece * toPiece <= 0)
                        list.add(move);
                }
                break;
            }
            case HORSE:
            case HORSE_HORSE: {
                Point[] moveList = {new Point(2, 1), new Point(1, 2), new Point(-1, 2), new Point(-2, 1),
                                    new Point(-2,-1), new Point(-1, -2), new Point(1, -2), new Point(2, -1)};
                for (Point i : moveList) {
                    Point move = new Point(location);
                    move.x += i.x;
                    move.y += i.y;
                    if(!locationIsLegal(move, 9, 0, 0, 8))
                        continue;
                    if(getChessBoard(location.x+i.x/2,location.y+i.y/2)!=BLANK)
                        continue;   // Lame Horse.
                    byte toPiece=getChessBoard(move.x, move.y);
                    if (choosePiece * toPiece <= 0||(absChoosePiece==HORSE&&abs(toPiece)>3&&abs(toPiece)<8))
                        list.add(move);
                }
                break;
            }
            case CHARIOT:
            case CHARIOT_CHARIOT: {
                Point[] moveList = {new Point(1, 0), new Point(-1, 0), new Point(0, -1), new Point(0, 1)};
                for(Point i : moveList){
                    Point move = new Point(location);
                    byte toPiece;
                    while(true){
                        move.x += i.x;
                        move.y += i.y;
                        if(!locationIsLegal(move, 9, 0, 0, 8))
                            break;
                        toPiece=getChessBoard(move.x, move.y);
                        if(toPiece==BLANK)
                            list.add(new Point(move));
                        else if (choosePiece * toPiece < 0||(absChoosePiece==CHARIOT&&abs(toPiece)>3&&abs(toPiece)<8)) {
                            list.add(move);
                            break;
                        }
                        else break;
                    }
                }
                break;
            }
            case CANNON:
            case CANNON_CANNON:{
                Point[] moveList = {new Point(1, 0), new Point(-1, 0), new Point(0, -1), new Point(0, 1)};
                for(Point i : moveList){
                    Point move = new Point(location);
                    byte toPiece;
                    boolean moveCannon=true;
                    while(true){
                        move.x += i.x;
                        move.y += i.y;
                        if(!locationIsLegal(move, 9, 0, 0, 8))
                            break;
                        toPiece=getChessBoard(move.x, move.y);
                        if(toPiece==BLANK) {
                            if (moveCannon)
                                list.add(new Point(move));
                        }
                        else if(moveCannon){
                            if(choosePiece * toPiece > 0 &&(absChoosePiece==CANNON&&abs(toPiece)>3&&abs(toPiece)<8))
                                list.add(new Point(move));
                            moveCannon=false;
                        }
                        else if(choosePiece * toPiece < 0||(absChoosePiece==CANNON&&abs(toPiece)>3&&abs(toPiece)<8)){
                            list.add(move);
                            break;
                        }
                        else break;
                    }
                }
                break;
            }
            case PAWN:
            case PAWN_PAWN:{
                Point moveList[] = {new Point(1, 0), new Point(0, -1), new Point(0, 1)};
                if(typeIsBlue)
                    moveList[0].x=-1;
                boolean notPassRiver=typeIsBlue? (location.x>4? true:false):(location.x<5? true:false);
                for(Point i : moveList) {
                    Point move = new Point(location);
                    move.x += i.x;
                    move.y += i.y;
                    if(!locationIsLegal(move, 9, 0, 0, 8))
                        continue;
                    byte toPiece=getChessBoard(move.x, move.y);
                    if (choosePiece * toPiece <= 0||(absChoosePiece==PAWN&&abs(toPiece)>3&&abs(toPiece)<8))
                        list.add(move);
                    if(notPassRiver) break; // Pawn do not pass the river.
                }
                break;
            }
            default:
                // Divide Pieces and transfer them to HORSE_HORSE, CHARIOT_CHARIOT, CANNON_CANNON, PAWN_PAWN.
                byte absPiece1=(byte)abs(dividePieces[absChoosePiece-8][0]-8);
                byte absPiece2=(byte)abs(dividePieces[absChoosePiece-8][1]-8);
                absPiece1=(byte)(18-(absPiece1*absPiece1+absPiece1)/2);
                absPiece2=(byte)(18-(absPiece2*absPiece2+absPiece2)/2);
                byte piece1=typeIsBlue? (byte)-absPiece1:absPiece1;
                byte piece2=typeIsBlue? (byte)-absPiece2:absPiece2;

                list.addAll(getMoveList(location,piece1)); // The piece have two ability.
                list.addAll(getMoveList(location,piece2));
                if(absPiece2==PAWN_PAWN){ // Remove illegal Point about pawn
                    boolean notPassRiver=typeIsBlue? (location.x>4? true:false):(location.x<5? true:false);
                    Iterator<Point> listIterator = list.iterator();
                    while(listIterator.hasNext()){
                        Point tmp=listIterator.next(); // The temp Point save the value of list.
                        // Pawn only go up.
                        if(typeIsBlue? (location.x<tmp.x? true:false):(location.x>tmp.x? true:false)) {
                            listIterator.remove();
                            continue;
                        }
                        // The piece can not move to left or right if it have not passed river.
                        if(notPassRiver){
                            int subY=abs(location.y-tmp.y);
                            if(absPiece1!=HORSE_HORSE&&subY>0||subY>1)
                                listIterator.remove();
                        }
                    }
                }
                break;
        }
        return list;
    }

    /**
     * This function is to move pieces.
     * @param from The location of piece that from.
     * @param to The Location of piece that to.
     * @param choosePiece The piece which you want to move.
     */
    public void movePiece(Point from,Point to,byte choosePiece){
        boolean typeIsBlue=choosePiece<0;
        byte fromPiece=getChessBoard(from.x,from.y);
        byte toPiece=getChessBoard(to.x,to.y);

        chessRecordsStack.push(new ChessRecord(from,to,choosePiece,toPiece));

        if(choosePiece==fromPiece) // Move pieces
            setChessBoard(from.x,from.y,BLANK);
        else { //Divide pieces
            int index=abs(fromPiece)-8;
            byte piece=dividePieces[index][0] != abs(choosePiece) ?
                    dividePieces[index][0] : dividePieces[index][1];
            setChessBoard(from.x, from.y,typeIsBlue? (byte)-piece:piece);
        }
        if(choosePiece*toPiece<=0) // Eat pieces
            setChessBoard(to.x,to.y,choosePiece);
        else{ //Fusion pieces
            byte piece=fusionPieces[abs(choosePiece)-4][abs(toPiece)-4];
            setChessBoard(to.x,to.y,typeIsBlue? (byte)-piece:piece);
        }
    }

    public ChessRecord getRecord(){
        if(isCanUndo()) {
            return chessRecordsStack.lastElement();
        }
        else return null;
    }

    /**
     *  This function is for judging if we can undo.
     * @return True or false.
     */
    public boolean isCanUndo(){
        return (!chessRecordsStack.empty());
    }

    /**
     * This function is to undo pieces.
     */
    public boolean undoPiece(){
        if(!isCanUndo())
            return false; // Return when the stack is empty.

        ChessRecord record=chessRecordsStack.pop();
        Point from=record.getFrom();
        Point to=record.getTo();
        byte fromPiece=getChessBoard(from.x,from.y);
        byte choosePiece=record.getChoosePiece();
        byte toPiece=record.getToPiece();

        setChessBoard(to.x,to.y,toPiece);
        if(fromPiece==BLANK)
            setChessBoard(from.x,from.y,choosePiece);
        else{   //Fusion pieces
            boolean typeIsBlue=choosePiece<0;
            byte piece=fusionPieces[abs(choosePiece)-4][abs(fromPiece)-4];
            setChessBoard(from.x,from.y,typeIsBlue? (byte)-piece:piece);
        }
        return true;
    }

    /**
     * This function is used to judge if a King is checked.
     * @param typeIsRed What color you want to judge.
     * @return True or False
     */
    public boolean isKingChecked(boolean typeIsRed){
        byte judgeKing=typeIsRed? KING:(byte)-KING; // The King will be judged.
        for(int i=0;i<10;i++){
            for(int j=0;j<9;j++){
                byte piece=getChessBoard(i,j);
                if(piece==BLANK||abs(piece)==ADVISER||abs(piece)==ELEPHANT) // Continue when piece is blank.
                    continue;
                if((piece<0)==typeIsRed){
                    Set<Point> moveList=getMoveList(new Point(i,j),piece);
                    for(Point point:moveList){
                        if(getChessBoard(point.x,point.y)==judgeKing)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * This function is used to judge if a King is checked out.
     * @param typeIsRed What color you want to judge.
     * @return True or False
     */
    public boolean isWin(boolean typeIsRed){
        for(int i=0;i<10;i++){
            for(int j=0;j<9;j++){
                byte piece=getChessBoard(i,j);
                if(piece==BLANK)
                    continue;
                if((piece<0)==typeIsRed){
                    Set<Byte> pieces=new HashSet();// A set about pieces at Point(i,j).
                    pieces.add(new Byte(piece));
                    int absPiece=abs(piece);
                    if(absPiece>7){// Add the divided pieces.
                        pieces.add(typeIsRed? new Byte((byte)-dividePieces[absPiece-8][0]):
                                new Byte(dividePieces[absPiece-8][0]));
                        pieces.add(typeIsRed? new Byte((byte)-dividePieces[absPiece-8][1]):
                                new Byte(dividePieces[absPiece-8][1]));
                    }
                    for(Byte tmp:pieces){
                        Set<Point> moveList=getMoveList(new Point(i,j),tmp);
                        for(Point point:moveList){
                            movePiece(new Point(i,j),new Point(point),tmp);
                            if(!isKingChecked(!typeIsRed)) {// Return false if the King is not checked.
                                undoPiece();// Undo piece.
                                return false;
                            }
                            undoPiece();// Undo piece.
                        }
                    }
                }
            }
        }
        return true;
    }

    // Get value of one piece.
    private int getPieceValue(byte piece,Point location){
        if(piece!=BLANK) {
            boolean typeIsBlue = piece < 0;
            int x = typeIsBlue ? location.x : 9 - location.x;
            int y = typeIsBlue ? location.y : 8 - location.y;// Switch location when type is red
            int absPiece = abs(piece);
            if (absPiece < 8) {// Normal pieces.
                return value[absPiece-1][x][y];
            }
            else{// Fusion pieces.
                byte piece1=dividePieces[absPiece-8][0];
                byte piece2=dividePieces[absPiece-8][1];
                return value[piece1-1][x][y]+value[piece2-1][x][y];
            }
        }
        return 0;
    }

    private List<AutoList> getAutoMoveList(Point from, byte choosePiece){
        List<AutoList> list=new ArrayList<>();
        Set<Point> moveList=getMoveList(new Point(from),choosePiece);
        for(Point point:moveList){
            byte toPiece=getChessBoard(point.x,point.y);
            int value=choosePiece*toPiece<0? getPieceValue(toPiece,point):
                0+getPieceValue(choosePiece,point)-getPieceValue(choosePiece,from);
            list.add(new AutoList(point,value));
        }
        return list;
    }

    /**
     * This function will auto move a piece.
     * @param typeIsRed What color pieces you want you move.
     */
    public ChessRecord autoMovePiece(boolean typeIsRed){
        return autoMovePiece(typeIsRed,2,1,null);
    }

    public ChessRecord autoMovePiece(boolean typeIsRed,int maxDeep,int currentDeep,Integer cutValue){
        boolean isAdd=(currentDeep%2==1);// Add or Sub.
        int maxValue=0;
        Integer nextCutValue=null;
        ChessRecord theRecord=null;
        List<AutoPiece> autoPieces=new ArrayList<>();
        for(int i=0;i<10;i++){
            for(int j=0;j<9;j++){
                byte piece=getChessBoard(i,j);
                Point location=new Point(i,j);
                if(piece!=BLANK&&(piece>0)==typeIsRed){
                    autoPieces.add(new AutoPiece(piece,location));// Get pieces which can move.
                    int absPiece=abs(piece);
                    if(absPiece>7){// Add the divided pieces.
                        byte piece1=typeIsRed? dividePieces[absPiece-8][0]:(byte)-dividePieces[absPiece-8][0];
                        byte piece2=typeIsRed? dividePieces[absPiece-8][1]:(byte)-dividePieces[absPiece-8][1];
                        autoPieces.add(new AutoPiece(piece1,location));
                        autoPieces.add(new AutoPiece(piece2,location));
                    }
                }
            }
        }
        Collections.sort(autoPieces);// Sort for search tree quickly.
        for(AutoPiece thePiece:autoPieces){
            byte piece=thePiece.getPiece();
            Point location=thePiece.getLocation();
            List<AutoList> moveList=getAutoMoveList(new Point(location),piece);
            Collections.sort(moveList);// Sort for search tree quickly.
            for(AutoList tmpList:moveList){
                Point point=tmpList.getLocation();
                int value=tmpList.getValue();
                movePiece(new Point(location),new Point(point),piece);
                ChessRecord record=chessRecordsStack.lastElement();// Get record of the step.
                int tmpValue=isAdd? value:-value;
                Integer nextTempValue=nextCutValue;// Temp instead of nextCutValue.
                if(currentDeep<maxDeep){
                    ChessRecord tmpRecord=autoMovePiece(!typeIsRed,maxDeep,currentDeep+1,nextCutValue);// Next note.
                    if(tmpRecord!=null){
                        tmpValue+=tmpRecord.getValue();
                        if(nextCutValue==null||(isAdd&&nextCutValue<tmpRecord.getValue())||(!isAdd&&nextCutValue>tmpRecord.getValue())){
                            nextTempValue= new Integer(tmpRecord.getValue());// Get nextCutValue for cut bunch.
                        }
                    }
                }
                if(theRecord==null||(isAdd&&maxValue<tmpValue)||(!isAdd&&maxValue>tmpValue)){
                    if(isKingChecked(typeIsRed)) {// Return false if the King is not checked.
                        undoPiece();// Undo piece.
                        continue;
                    }
                    theRecord=record;
                    theRecord.setValue(tmpValue);
                    maxValue=tmpValue;
                }
                if(nextTempValue!=null) {
                    nextCutValue = nextTempValue;// NextCutValue for cut bunch.
                }
                undoPiece();// Undo piece.
                if(cutValue!=null&&((isAdd&&cutValue<maxValue)||(!isAdd&&cutValue>maxValue))){
                    return theRecord;// Cut bunch.
                }
            }
        }
        return theRecord;
    }
}

// The class for sort of pieces what will auto move.
class AutoPiece implements Comparable<AutoPiece>{
    private byte piece;
    private Point location;

    AutoPiece(byte piece,Point location){
        this.piece=piece;
        this.location=location;
    }

    @Override
    public int compareTo(AutoPiece autoPiece) {
        return (abs(autoPiece.getPiece())-abs(this.piece));
    }

    public byte getPiece() {
        return piece;
    }

    public Point getLocation() {
        return location;
    }
}

class AutoList implements Comparable<AutoList>{
    private Point location;
    private int value;

    AutoList(Point location, int value){
        this.location=location;
        this.value=value;
    }

    @Override
    public int compareTo(AutoList autoList) {
        return autoList.getValue()-this.getValue();
    }

    public Point getLocation() {
        return location;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value){
        this.value=value;
    }
}