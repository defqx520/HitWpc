package cn.edu.hit.ftcl.wearablepc.VoiceModule;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import cn.edu.hit.ftcl.wearablepc.R;

/**
 * 自定义Button
 */
public class AudioRecorderButton extends AppCompatButton {
    // 按钮正常状态（默认状态）
    private static final int STATE_NORMAL = 1;
    //正在录音状态
    private static final int STATE_RECORDING = 2;
    //录音取消状态
    private static final int STATE_CANCEL = 3;
    //记录当前状态
    private int mCurrentState = STATE_NORMAL;
    //是否开始录音标志
    private boolean isRecording = false;
    //判断在Button上滑动距离，以判断 是否取消
    private static final int DISTANCE_Y_CANCEL = 50;
    //对话框管理工具类
    private DialogManager mDialogManager;
    //录音管理工具类
    private AudioManager mAudioManager;
    //记录录音时间
    private long mTime;
    // 是否触发longClick
    private boolean mReady;
    //录音准备
    private static final int MSG_AUDIO_PREPARED = 0x110;
    //音量发生改变
    private static final int MSG_VOICE_CHANGED = 0x111;
    //取消提示对话框
    private static final int MSG_DIALOG_DIMISS = 0x112;

    /**
     *  获取音量大小的线程
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {

        public void run() {
            while (isRecording) {//判断正在录音
                try {
                    Thread.sleep(100);
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);//每0.1秒发送消息
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //显示对话框
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    //更新声音
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    //取消对话框
                    mDialogManager.dimissDialog();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(context);
        //录音文件存放地址
        String dir = Environment.getExternalStorageDirectory() + "/HitWearable/voice";
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(new AudioManager.AudioStateListener() {
            public void wellPrepared() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
            }
        });

        // 由于这个类是button所以在构造方法中添加监听事件
        setOnLongClickListener(new OnLongClickListener() {

            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

 /**
   *  录音完成后的回调
   */
    public interface AudioFinishRecorderCallBack {
        void onFinish(long seconds, String filePath);
    }

    private AudioFinishRecorderCallBack finishRecorderCallBack;

    public void setFinishRecorderCallBack(AudioFinishRecorderCallBack listener) {
        finishRecorderCallBack = listener;
    }

    /**
     * 处理Button的OnTouchEvent事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取TouchEvent状态
        int action = event.getAction();
        // 获得x轴坐标
        int x = (int) event.getX();
        // 获得y轴坐标
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN://手指按下
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                if (isRecording) {
                    //根据x,y的坐标判断是否需要取消
                    if (wantToCancle(x, y)) {
                        changeState(STATE_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP://手指放开
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (mCurrentState == STATE_RECORDING) {
                    //如果状态为正在录音，则结束录制
                    mDialogManager.dimissDialog();
                    mAudioManager.release();

                    if (finishRecorderCallBack != null) {
                        mTime = System.currentTimeMillis();
                        finishRecorderCallBack.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }

                } else if (mCurrentState == STATE_CANCEL) { // 想要取消
                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isRecording = false;
        mTime = 0;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancle(int x, int y) {
        // 超过按钮的宽度
        if (x < 0 || x > getWidth()) {
            return true;
        }
        // 超过按钮的高度
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
    }


    /**
     *  根据状态改变Button显示
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;

                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_recording);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;

                case STATE_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    mDialogManager.wantToCancel();
                    setText(R.string.str_recorder_want_cancel);
                    break;
            }
        }
    }
}
