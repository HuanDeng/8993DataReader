package huan.a8993datareader;

        import android.media.AudioFormat;
        import android.media.AudioRecord;
        import android.media.AudioRecord.OnRecordPositionUpdateListener;
        import android.media.MediaRecorder;
        import android.util.Log;
public class AudioRecordRx {
    private static final int sampleRate=44100;
    private static final int audioRecordRxChannel = AudioFormat.CHANNEL_IN_MONO;
    private static final int audioRecordRxFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static final int audioSource = MediaRecorder.AudioSource.MIC;
    public static boolean audioReachedFlag = false;
    public static boolean audioRecordFlag = false;
    public static int audioRecordReadFlag;
    AudioRecordRxThread audioRecordRxThread = null;
    DecoderRx decoderRx = new DecoderRx();
    public static AudioRecord audioRecord;
    //public static int minAudioRecordRxBufSize = AudioRecord.getMinBufferSize(sampleRate, audioRecordRxChannel,audioRecordRxFormat);
    public static int minAudioRecordRxBufSize=1024*24;
    public static int audioRecordRxBufSize = minAudioRecordRxBufSize * 4;
    short[] audioRecordRxBuf=new short[minAudioRecordRxBufSize];

    private OnRecordPositionUpdateListener audioRecordListener=new OnRecordPositionUpdateListener(){//缓冲区溢出中断函数
        @Override
        public void onMarkerReached(AudioRecord recorder)
        {
            // TODO Auto-generated method stub
            Log.d(null, "AudioMarkerReached");
        }
        @Override
        public void onPeriodicNotification(AudioRecord recorder)
        {
            // TODO Auto-generated method stub
            audioReachedFlag=true;
            Log.d(null, "AudioRecordReached");
            //缓冲区溢出中断到来，比如read缓冲区数据才可以接续录音，否则会阻塞
            //在这里并不read数据，否则会占用主线程的资源，造成activity反应很慢，read函数在Decoder.java那里
            //  System.out.println("record interrupt " + System.currentTimeMillis());
        }

    };

    public short get8993Vel()
    {
        return decoderRx._8993Vel;
    }
    public short get8993Rh()
    {
        return decoderRx._8993Rh;
    }
    public  short get8993Tair()
    {
        return decoderRx._8993Tair;
    }
    public  short get8993Bat()
    {
        return decoderRx._8993Bat;
    }
    /********************************
     * 设置好中断事件，初始化硬件建立AudioRecordRx对象
     */
    public void start(){
        audioRecordFlag=false;
        if(audioRecordRxThread ==null){
            audioRecordFlag=true;
            audioRecordRxThread = new AudioRecordRxThread(audioRecord,minAudioRecordRxBufSize);//minRecaudioRecordRxBufSize
            audioRecordRxThread.start();
        }
    }
    /******************************
     * 停止录音，释放资源
     */
    public void stop(){
        audioRecordFlag=false;
        try {
            AudioRecordRxThread.sleep(20);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        audioRecordRxThread.interrupt();
        try {
            audioRecordRxThread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        audioRecordRxThread =null;
        if(audioRecord!=null)
        {
            audioRecord.release();
            audioRecord=null;
        }
    }
    /********************************
     * 录音线程 不断读取录音PCM数据进行解码
     * @author Administrator
     *
     */
    public class AudioRecordRxThread extends Thread{
        int bufSize;
        String bytetostring;
        public AudioRecordRxThread(AudioRecord audioRecord,int bufferSize){
            this.bufSize=bufferSize;
        }
        public void run(){
            audioRecord=new AudioRecord(audioSource,sampleRate, audioRecordRxChannel,audioRecordRxFormat,audioRecordRxBufSize);
            audioRecord.setPositionNotificationPeriod(minAudioRecordRxBufSize); //这是监听器，当缓冲区为minRecBufSize溢出时中断
            audioRecord.setRecordPositionUpdateListener(audioRecordListener);//中断服务函数
            audioRecord.startRecording();
            audioRecordReadFlag = audioRecord.read(audioRecordRxBuf, 0,minAudioRecordRxBufSize);
            decoderRx.decoderAudioRxbuf();
        }
    }


    /*******************************
     * 打印录音PCM数据，用于程序的调试
     * @param tmpBuf
     * @param ret
     */
    public void println_PCM(short[] tmpBuf,int ret )
    {
        for(int i=0;i<ret;i++)
        {
            System.out.println("PCM short " + i + "is" + tmpBuf[i]);
        }
    }

}
