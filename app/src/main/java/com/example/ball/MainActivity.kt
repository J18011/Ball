package com.example.ball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), SensorEventListener,SurfaceHolder.Callback{
    //センサーの変数宣言
    private var mSensorManager:SensorManager by Delegates.notNull<SensorManager>()
    private var mAccSensor:Sensor by Delegates.notNull<Sensor>()
    private var mHolder:SurfaceHolder by Delegates.notNull<SurfaceHolder>()
    private var mSurfaceWidth:Int by Delegates.notNull<Int>()
    private var mSurfaceHeight:Int by Delegates.notNull<Int>()
    private var mBallx:Float =0F
    private var mBally:Float =0F
    private var mVX:Float=0F
    private var mVY:Float=0F
    private var mFrom:Long=0L
    private var mTo:Long=0L
    private val COEF:Float = 1000.0F //移動量ね
    private val RADIUS:Float = 50.0F //半径
    var x:Float = 0.0F
    var y:Float = 0.0F
    var z:Float = 0.0F
    var t:Float = 0.0F
    var dx:Float=0F
    var dy:Float=0F

    //ゴール関係
    private var flg:Boolean =false

    var left:Int =10
    var top:Int =100
    var right:Int =300
    var bottom:Int =200



    override fun surfaceCreated(holder: SurfaceHolder) {
        mFrom = System.currentTimeMillis()
        mSensorManager.registerListener(this,mAccSensor,SensorManager.SENSOR_DELAY_GAME)

    }
    private fun drawCanvas(){
        var c:Canvas = mHolder.lockCanvas()
        c.drawColor(Color.YELLOW)
        var paint:Paint = Paint()
        paint.setColor(Color.MAGENTA)
        c.drawCircle(mBallx,mBally,RADIUS,paint)
        paint.setColor(Color.BLUE)
        var rect:Rect = Rect(left,top,right,bottom)
        c.drawRect(rect,paint)
        if(flg == true){
            c.drawText("HelloText",100F,100F,paint)
        }
        mHolder.unlockCanvasAndPost(c)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mSensorManager.unregisterListener(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        mSurfaceWidth = i1
        mSurfaceHeight = i2
        mBallx = i1 / 2F
        mBally = i2 / 2F
        mVX = 0F
        mVY = 0F
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //requestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_main)

        //センサーマネージャの値を取得
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //加速度計のセンサーの値を取得する
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        var surfaceView:SurfaceView
        surfaceView= findViewById(R.id.surfaceView)
        mHolder =surfaceView.holder
        mHolder.addCallback(this)
    }

    //センサーの精度に変更があった場合呼び出される
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    //センサーの値に変更があった場合呼び出される
    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("Sensormanager", "----------")
        Log.d("x",event?.values!![0].toString())
        Log.d("y",event?.values!![1].toString())
        Log.d("z",event?.values!![2].toString())

        x = event?.values!![0]*-1
        y = event?.values!![1]
        z = event?.values!![2]
        mTo = System.currentTimeMillis()
        t = (mTo - mFrom).toFloat()
        t = t/1000.0F

        dx = mVX * t + x * t * t / 2.0F
        dy = mVY * t + y * t * t / 2.0F
        mBallx = mBallx + dx * COEF
        mBally = mBally + dy * COEF
        mVX = mVX + x *t
        mVY = mVY + y *t

        if (mBallx - RADIUS < 0 && mVX  < 0){
            mVX = -mVX/1.5F
            mBallx = RADIUS
        }else if(mBallx + RADIUS > mSurfaceWidth && mVX > 0){
            mVX = -mVX /1.5F
            mBallx = mSurfaceWidth - RADIUS
        }
        if(mBally - RADIUS < 0 && mVY < 0){
            mVY = -mVY / 1.5f
            mBally = RADIUS
        }else if(mBally + RADIUS > mSurfaceHeight && mVY > 0){
            mVY = -mVY / 1.5f
            mBally = mSurfaceHeight - RADIUS
        }
        mFrom = System.currentTimeMillis()
        drawCanvas()

        if(left <= mBallx + RADIUS && right >= mBallx - RADIUS
            && top <= mBally + RADIUS && bottom >= mBally - RADIUS){
            flg = true;
        }else{
            flg = false;
        }

    }

    //Resumeセンサー稼働監視
    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this,mAccSensor,SensorManager.SENSOR_DELAY_GAME)

    }

    //Pauseセンサー監視終了
    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }
}
