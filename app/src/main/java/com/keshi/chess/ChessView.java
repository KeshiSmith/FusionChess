package com.keshi.chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jjtx.bluetoothutil.util.BluetoothHelper;

import java.util.Set;

import static java.lang.Math.abs;

public class ChessView extends View {
    private GameActivity activity;//游戏主Activity
    private FusionChess fusionChess;//融合象棋类
    private BluetoothHelper bluetoothHelper;//蓝牙类
    private boolean isAdmit;//游戏状态：认输
    private boolean myTypeIsRed;//我方颜色是否为红色
    private boolean isRedGo;//当前是否为红色先行
    private boolean focus;//当前是否有棋子选中
    private byte fromPiece;//起始位置的棋子
    private byte choosePiece;//当前选中的棋子
    private int chooseIndex;//选中棋子位置-0,1,2
    private int fromX,fromY;//当前棋子的开始位置
    private boolean showPossibleMoves;//显示可移动位置
    private Set<Point> moveList;//移动列表
    private Bitmap chessboard;//棋盘图片
    private Bitmap pieces[]=new Bitmap[34];//棋子
    private Bitmap chooseImage;//选中框
    private Bitmap recordImage;//记录框
    private int viewWidth,chessWidth;//视图大小
    private static int pieceResId[]={//棋子资源ID
            R.drawable.rk,  R.drawable.ra,  R.drawable.rb,  R.drawable.rn,
            R.drawable.rr,  R.drawable.rc,  R.drawable.rp,  R.drawable.rnn,
            R.drawable.rnr, R.drawable.rnc, R.drawable.rnp, R.drawable.rrr,
            R.drawable.rrp, R.drawable.rcr, R.drawable.rcc, R.drawable.rcp, R.drawable.rpp,
            R.drawable.bk,  R.drawable.ba,  R.drawable.bb,  R.drawable.bn,
            R.drawable.br,  R.drawable.bc,  R.drawable.bp,  R.drawable.bnn,
            R.drawable.bnr, R.drawable.bnc, R.drawable.bnp, R.drawable.brr,
            R.drawable.brp, R.drawable.bcr, R.drawable.bcc, R.drawable.bcp, R.drawable.bpp};

    public ChessView(Context context, AttributeSet attrs,int defStyle) {
        super(context,attrs,defStyle);
    }
    public ChessView(Context context,AttributeSet attrs) {
        super(context,attrs);
        //初始化游戏
        fusionChess=new FusionChess();//融合象棋类初始化
        isAdmit=false;//游戏中
        myTypeIsRed=true;//我方棋子是否为红色
        isRedGo=true;//红色先行
        focus=false;//未选中
        showPossibleMoves=false;//不显示路径
    }
    public ChessView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth=w;//获取视图宽度
        chessWidth=viewWidth/9;//获取相关布局大小
        chooseImage=BitmapFactory.decodeResource(getResources(),R.drawable.choose);//棋子选中资源
        recordImage=BitmapFactory.decodeResource(getResources(),R.drawable.record);//棋子路径资源
        float scaleWidth=((float)chessWidth/chooseImage.getWidth());//棋子缩放比例
        Matrix matrixPieces=new Matrix();//棋子缩放矩阵
        matrixPieces.postScale(scaleWidth,scaleWidth);
        for(int i=0;i<pieces.length;i++){//棋子资源
            Bitmap tmp= BitmapFactory.decodeResource(getResources(),pieceResId[i]);
            pieces[i]= Bitmap.createBitmap(tmp,0,0,tmp.getWidth(),tmp.getHeight(),matrixPieces,true);//按照屏幕对棋子缩放
        }
        chooseImage= Bitmap.createBitmap(chooseImage,0,0,chooseImage.getWidth(),chooseImage.getHeight(),matrixPieces,true);
        recordImage= Bitmap.createBitmap(recordImage,0,0,recordImage.getWidth(),recordImage.getHeight(),matrixPieces,true);
        //棋盘资源加载
        chessboard= BitmapFactory.decodeResource(getResources(),R.drawable.board);//棋盘资源
        scaleWidth=((float)viewWidth/chessboard.getWidth());//棋盘缩放比例
        Matrix matrixBoard=new Matrix();//棋盘缩放矩阵
        matrixBoard.postScale(scaleWidth,scaleWidth);
        chessboard= Bitmap.createBitmap(chessboard,0,0,chessboard.getWidth(),chessboard.getHeight(),matrixBoard,true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(chessboard,0,0,null);
        for(int i=0;i<10;i++){
            for(int j=0;j<9;j++){
                //获取坐标(i,j)的棋子
                byte piece=fusionChess.getChessBoard(i,j);
                if(piece!=FusionChess.BLANK){
                    int x=myTypeIsRed? 9-i:i;
                    int y=myTypeIsRed? 8-j:j;//我方为红色时坐标切换
                    //绘画棋盘
                    canvas.drawBitmap(pieces[piece>0? piece-1:16-piece],chessWidth*y,chessWidth*x,null);
                }
            }
        }

        if(!fusionChess.chessRecordsStack.empty()){//绘画上次路径
            ChessRecord record=fusionChess.chessRecordsStack.lastElement();//获取记录
            Point from=new Point(record.getFrom());//起始点
            Point to=new Point(record.getTo());//目标点
            if(myTypeIsRed){
                from.x=9-from.x;
                from.y=8-from.y;
                to.x=9-to.x;
                to.y=8-to.y;//我方为红色时坐标切换
            }
            canvas.drawBitmap(recordImage,chessWidth*from.y,chessWidth*from.x,null);//绘画路径
            canvas.drawBitmap(recordImage,chessWidth*to.y,chessWidth*to.x,null);//绘画路径
        }

        if(focus==true) {//绘画棋子焦点
            int x=myTypeIsRed? 9-fromX:fromX;
            int y=myTypeIsRed? 8-fromY:fromY;//我方为红色时坐标切换
            canvas.drawBitmap(chooseImage, chessWidth*y, chessWidth*x, null);//绘画选中框

            //绘画状态栏
            byte absFromPiece=(byte)abs(fromPiece);
            if(absFromPiece<8){//当为普通棋子时居中放置
                canvas.drawBitmap(pieces[fromPiece>0? fromPiece-1:16-fromPiece],chessWidth*4,(int)(chessWidth*10.5),null);
                canvas.drawBitmap(chooseImage, chessWidth*4, (int)(chessWidth*10.5), null);
            }
            else{//为融合棋子时横排放置
                canvas.drawBitmap(pieces[fromPiece>0? fromPiece-1:16-fromPiece],chessWidth*3,(int)(chessWidth*10.5),null);
                byte piece1=fromPiece>0? FusionChess.dividePieces[absFromPiece-8][0]:(byte)-FusionChess.dividePieces[absFromPiece-8][0];
                byte piece2=fromPiece>0? FusionChess.dividePieces[absFromPiece-8][1]:(byte)-FusionChess.dividePieces[absFromPiece-8][1];
                canvas.drawBitmap(pieces[piece1>0? piece1-1:16-piece1],chessWidth*4,(int)(chessWidth*10.5),null);
                canvas.drawBitmap(pieces[piece2>0? piece2-1:16-piece2],chessWidth*5,(int)(chessWidth*10.5),null);
                canvas.drawBitmap(chooseImage, chessWidth*(chooseIndex+3), (int)(chessWidth*10.5), null);//绘画选中框
            }
            if(showPossibleMoves){
                for(Point i:moveList){//绘画可行走路径
                    x=myTypeIsRed? 9-i.x:i.x;
                    y=myTypeIsRed? 8-i.y:i.y;//我方为红色时坐标切换
                    canvas.drawBitmap(chooseImage, chessWidth*y, chessWidth*x, null);//绘画选中框
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(myTypeIsRed!=isRedGo||isAdmit) return true;//对方回合
        int x=(int)(event.getY()/chessWidth);//获取棋盘坐标
        int y=(int)(event.getX()/chessWidth);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://获取点击事件
                if(x<10){
                    if(myTypeIsRed){
                        x=9-x;
                        y=8-y;//我方为红色坐标变换
                    }
                    byte clickPiece=fusionChess.getChessBoard(x,y);//获取点击的棋子
                    if(focus&&moveList!=null&&moveList.contains(new Point(x,y))){
                        movePiece(new Point(fromX,fromY),new Point(x,y),choosePiece);//移动棋子
                    }
                    else if(clickPiece!=FusionChess.BLANK&&isRedGo==clickPiece>0){
                        focus=true;//选中棋子
                        fromX=x;
                        fromY=y;
                        fromPiece=choosePiece=clickPiece;//获取选中的棋子
                        chooseIndex=0;
                    }
                    else{
                        focus=false;//取消选中
                        invalidate();//刷新视图
                    }
                }
                else if(focus){//选中状态栏
                    byte absFromPiece=(byte)abs(fromPiece);
                    if(absFromPiece<8){//是否为普通棋子
                        if(y==4) {
                            chooseIndex = 0;
                        }
                        else{
                            focus=false;//取消选中
                            invalidate();//刷新视图
                        }
                    }
                    else if(y==3) {//特殊棋子未分离
                        chooseIndex=0;
                        choosePiece=fromPiece;
                    }
                    else if(y==4||y==5){//特殊棋子分离
                        chooseIndex = y - 3;//更改选中棋子
                        choosePiece = fromPiece>0? FusionChess.dividePieces[absFromPiece-8][chooseIndex-1]:
                                (byte)-FusionChess.dividePieces[absFromPiece-8][chooseIndex-1];
                    }
                    else{
                        focus=false;//取消选中
                        invalidate();//刷新视图
                    }
                }
                if(focus){
                    moveList=fusionChess.getMoveList(new Point(fromX,fromY),choosePiece);//获取移动列表
                    playSound(R.raw.pick);//播放选中棋子音效
                    invalidate();//刷新视图
                }
                break;
        }
        return true;
    }

    //开始新游戏
    public void newGame(boolean myTypeIsRed){
        fusionChess.initChessBoard();//融合象棋盘初始化
        isAdmit=false;//游戏中
        this.myTypeIsRed=myTypeIsRed;//我方棋子是否为红色
        isRedGo=true;//红色先行
        focus=false;//未选中
        playSound(R.raw.new_game);//播放开始音效
        invalidate();//刷新视图

        String message=getContext().getString(myTypeIsRed? R.string.message_first:R.string.message_next);
        showMessage(message);//提示游戏开始

        if(!bluetoothHelper.isConnected()&&!myTypeIsRed){
            autoMovePiece();//红棋自动先行
        }
    }

    //移动棋子函数
    public void movePiece(Point from, Point to, byte choosePiece){
        focus=false;//取消选中
        fusionChess.movePiece(from,to,choosePiece);//移动棋子

        if(fusionChess.isKingChecked(isRedGo)){//判断自己是否被将
            fusionChess.undoPiece();//撤销棋子，行动作废
            String message=isRedGo? getContext().getString(R.string.message_red_not_move):
                    getContext().getString(R.string.message_blue_not_move);
            showMessage(message);//将死
            return;//退出移动函数
        }
        if(bluetoothHelper.isConnected()&&myTypeIsRed==isRedGo) {
            String message = String.format("%d %d %d %d %d", from.x, from.y, to.x, to.y, choosePiece);
            activity.sendMessage(message);
        }

        if(fusionChess.isKingChecked(!isRedGo)) {//判断对方是否被将
            if(fusionChess.isWin(isRedGo)) {//判断对方是否被将死
                String message=isRedGo==myTypeIsRed? getContext().getString(R.string.message_win):
                        getContext().getString(R.string.message_loss);
                showMessage(message);//将死
                playSound(isRedGo==myTypeIsRed? R.raw.win:R.raw.loss);//播放输赢音效

                if(bluetoothHelper.isConnected()){
                    isAdmit=true;//结束游戏
                    activity.closeConnect();// 关闭蓝牙连接
                }
            }
            else {
                playSound(R.raw.check);//播放将军音效
            }
        }
        else{
            playSound(R.raw.move);//播放移动棋子音效
        }
        isRedGo = !isRedGo;//轮流下棋
        invalidate();//刷新视图
        if(!bluetoothHelper.isConnected()){
            autoMovePiece();//对方下棋
        }
    }

    //自动认输函数
    public void admitGame(boolean whoIsMe){
        if(!isAdmit){
            isAdmit=true;//游戏认输
            String message=getContext().getString(whoIsMe?
                    R.string.message_admit_me:R.string.message_admit_you);
            showMessage(message);//认输
            playSound(whoIsMe? R.raw.loss:R.raw.win);//播放失败音效
        }
    }

    //判断是否可撤销棋子
    public boolean isCanUndo(){
        return (!fusionChess.chessRecordsStack.empty());
    }

    //撤销棋子函数
    public void undoPiece(boolean whoIsMe){
        if(isAdmit) return;
        focus=false;//取消选中
        boolean flag=whoIsMe? myTypeIsRed:!myTypeIsRed;
        do {
            if (fusionChess.undoPiece()) {
                playSound(R.raw.move);//播放移动棋子音效
                isRedGo = !isRedGo;//轮流下棋
            }
            else if(!myTypeIsRed&&!bluetoothHelper.isConnected()){
                autoMovePiece();//红棋自动先行
            }
            else break;
        }while(flag!=isRedGo);
        invalidate();//悔棋成功刷新视图
    }

    //自动移动棋子函数
    public void autoMovePiece(){
        ChessRecord record=fusionChess.autoMovePiece(isRedGo);
        if(record!=null){
            fusionChess.movePiece(record.getFrom(),record.getTo(),record.getChoosePiece());//移动棋子
            if(fusionChess.isKingChecked(!isRedGo)) {//判断对方是否被将
                if(fusionChess.isWin(isRedGo)) {//判断对方是否被将死
                    String message=isRedGo==myTypeIsRed? getContext().getString(R.string.message_win):
                            getContext().getString(R.string.message_loss);
                    showMessage(message);//将死
                    playSound(isRedGo==myTypeIsRed? R.raw.win:R.raw.loss);//播放输赢音效
                }
                else {
                    playSound(R.raw.check);//播放将军音效
                }
            }
            else{
                playSound(R.raw.move);//播放移动棋子音效
            }
            isRedGo=!isRedGo;//轮流下棋
            invalidate();//提示成功刷新视图
        }
    }

    //获取显示移动点状态
    public boolean getShowPossibleMoves(){
        return showPossibleMoves;
    }

    //设置是否显示可移动点
    public void setShowPossibleMoves(boolean showPossibleMoves){
        this.showPossibleMoves=showPossibleMoves;
        invalidate();//刷新视图
    }

    //获得游戏主Activity
    public void setActivity(GameActivity activity){
        this.activity=activity;
    }

    //显示消息函数
    public void showMessage(String message){
        activity.showMessage(message);
    }

    //播放音效函数
    public void playSound(int resid){
        activity.playSound(resid);
    }

    public void setBluetoothHelper(BluetoothHelper bluetoothHelper) {
        this.bluetoothHelper = bluetoothHelper;
    }
}
