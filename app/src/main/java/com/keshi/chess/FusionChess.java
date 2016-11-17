package com.keshi.chess;

import android.graphics.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
    private static final byte defaultBoard[][]= {
            {CHARIOT,    HORSE,      ELEPHANT,   ADVISER,    KING},
            {BLANK,      CANNON,     BLANK,      BLANK,      BLANK},
            {PAWN,       BLANK,      PAWN,       BLANK,      PAWN}};
    // Set static fusion-pieces array.(x,y)->HORSE,CHARIOT,CANNON,PAWN
    private static final byte fusionPieces[][]={
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
        //防止范围越界
        rangeU=rangeU>9? 9:rangeU;
        rangeD=rangeD<0? 0:rangeD;
        rangeL=rangeL<0? 0:rangeL;
        rangeR=rangeR>8? 8:rangeR;
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
        Set<Point> list=new HashSet<Point>();
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

        if(choosePiece==fromPiece)  // Move pieces
            setChessBoard(from.x,from.y,BLANK);
        else {   //Divide pieces
            int index=abs(fromPiece)-8;
            byte piece=dividePieces[index][0] != abs(choosePiece) ?
                    dividePieces[index][0] : dividePieces[index][1];
            setChessBoard(from.x, from.y,typeIsBlue? (byte)-piece:piece);
        }
        if(choosePiece*toPiece<=0)    // Eat pieces
            setChessBoard(to.x,to.y,choosePiece);
        else{   //Fusion pieces
            int index1=abs(choosePiece)-4;
            int index2=abs(toPiece)-4;
            byte piece=fusionPieces[index1][index2];
            setChessBoard(to.x,to.y,typeIsBlue? (byte)-piece:piece);
        }
    }

    /**
     * This function is to undo pieces.
     */
    public void undoPiece(){
        if(chessRecordsStack.empty())
            return; // Return when the stack is empty.

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
            choosePiece-=4; fromPiece-=4;
            setChessBoard(from.x,from.y,fusionPieces[choosePiece][fromPiece]);
        }
    }
}

// This class is used to save records.
class ChessRecord{
    private Point from;
    private Point to;
    private byte choosePiece,toPiece;

    public ChessRecord(Point from,Point to,byte fromPiece,byte toPiece){
        setFrom(from);
        setTo(to);
        setChoosePiece(fromPiece);
        setToPiece(fromPiece);
    }
//    public ChessRecord(int fromX,int fromY,int toX,int toY,byte fromPiece,byte toPiece){
//
//    }

    public Point getFrom() {
        return from;
    }

    public void setFrom(Point from) {
        this.from = from;
    }

    public Point getTo() {
        return to;
    }

    public void setTo(Point to) {
        this.to = to;
    }

    public byte getChoosePiece() {
        return choosePiece;
    }

    public void setChoosePiece(byte fromPiece) {
        this.choosePiece = fromPiece;
    }

    public byte getToPiece() {
        return toPiece;
    }

    public void setToPiece(byte toPiece) {
        this.toPiece = toPiece;
    }
}