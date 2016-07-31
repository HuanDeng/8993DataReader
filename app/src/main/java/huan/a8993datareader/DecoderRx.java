package huan.a8993datareader;


        import java.util.ArrayList;
        import android.media.AudioRecord;
        import android.os.Bundle;
        import android.os.Message;
        import android.util.Log;

public class DecoderRx {


    private int  counter_i = 0;
    private int  startIndex=0;
    public short highValue=6553;	//0-1阀值、反向小于500=1，大于500 0。

    short _1_Counter=0,_0_Counter=0;

    public short[] dataBitBuf=new short[1000];
    public short[] mqstDataBit=new short[72];

    public short _8993Vel=0;
    public short _8993Rh=0;
    public short _8993Tair=0;
    public short _8993Bat=0;
    public void decoderAudioRxbuf()
    {
        short currentSampleBit = 0;
        short lastSampleBit = -1;


        int audioRxBufLength = AudioRecordRx.minAudioRecordRxBufSize;
        short[] audioRxBuf = new short[audioRxBufLength];


        while(AudioRecordRx.audioRecordFlag)
        {
            if(AudioRecordRx.audioReachedFlag)
            {
                AudioRecordRx.audioReachedFlag = false;  //clear flag
                audioRxBufLength = AudioRecordRx.audioRecord.read(audioRxBuf, 0,AudioRecordRx.minAudioRecordRxBufSize);
                if(audioRxBufLength == AudioRecord.ERROR_BAD_VALUE)
                {
                    //reserved
                }
                else
                {
                    int dataBitStatus=0;//0 无效，1同相 2反相
                    int highCounter=0,mindCounter=0,lowCounter=0;
                    for(int idex=0;idex<audioRxBufLength;idex++)
                    {
                        short sampleValue = audioRxBuf[idex];
                        if(sampleValue>highValue)
                        {
                            if(mindCounter>500)
                            {
                                dataBitStatus=2;
                            }
                            highCounter++;
                            mindCounter=0;
                            lowCounter=0;
                        }
                        else if(sampleValue < -1*highValue)
                        {
                            if(mindCounter>500)
                            {
                                dataBitStatus=1;
                            }
                            lowCounter++;
                            mindCounter=0;
                            highCounter=0;
                        }
                        else
                        {
                            mindCounter++;
                            highCounter=0;
                            lowCounter=0;
                        }
                    }
                    _0_Counter=0;
                    _1_Counter=0;
                    int dataBitPtr=0;
                    boolean StartBitFlg=false;
                    if(dataBitStatus==2)
                    {
                        for (counter_i = 0; counter_i < audioRxBufLength; counter_i++)
                        {
                            short sampleValue = audioRxBuf[counter_i];
                            if (sampleValue < highValue)
                            {
                                currentSampleBit = 1;
                                _0_Counter = 0;
                                _1_Counter++;
                            } else
                            {
                                currentSampleBit = 0;
                                if (_1_Counter >= 500 && counter_i < 23570)
                                {
                                    StartBitFlg = true;
                                    startIndex = counter_i;
                                }
                                _1_Counter = 0;
                                _0_Counter++;
                            }
                            if(StartBitFlg)
                            {
                                if(dataBitPtr<1000)
                                {
                                    dataBitBuf[dataBitPtr]=currentSampleBit;
                                    dataBitPtr++;
                                }

                            }
                        }
                    }
                    if(dataBitStatus==1)
                    {
                        for(counter_i=0;counter_i<audioRxBufLength;counter_i++)
                        {
                            short sampleValue = audioRxBuf[counter_i];
                            if(sampleValue > -1* highValue)
                            {
                                currentSampleBit =1;
                                _0_Counter=0;
                                _1_Counter++;
                            }
                            else
                            {
                                currentSampleBit = 0;
                                if(_1_Counter>=500&&counter_i<23570)
                                {
                                    StartBitFlg=true;
                                    startIndex=counter_i;
                                }
                                _1_Counter=0;
                                _0_Counter++;
                            }
                            if(StartBitFlg)
                            {
                                if(dataBitPtr<1000)
                                {
                                    dataBitBuf[dataBitPtr]=currentSampleBit;
                                    dataBitPtr++;
                                }

                            }
                        }
                    }

                    if(StartBitFlg)
                    {
                        short[] BitArray=new short[72*2];
                        short[] BitTimeArray=new short[72*2];
                        short tempBit=0;
                        int j=0;
                        short datacounter=0;
                        for(int i=0;i<955;i++)//i 控制取样长度
                        {
                            if(dataBitBuf[i]==dataBitBuf[i+1])
                            {
                                datacounter++;
                            }
                            else
                            {
                                BitArray[j]=dataBitBuf[i];
                                BitTimeArray[j]=datacounter;
                                datacounter=0;
                                j++;
                            }
                            if(i==954)
                            {
                                BitArray[j]=dataBitBuf[i];
                                if(datacounter>8)datacounter=8;
                                BitTimeArray[j]=datacounter;
                                //j++;
                            }
                        }
                        boolean isDataTrue=true;
                        for(int i=0;i<j;i++)
                        {
                            if(BitTimeArray[i]>20)
                            {
                                isDataTrue=false;
                                break;
                            }
                        }
                        if(isDataTrue)
                        {
                            isDataTrue=false;
                            short[] SBitArray=new short[72*2];
                            int k=0;
                            for(int i=0;i<=j&&k<72*2;i++)
                            {
                                try
                                {
                                    if(BitTimeArray[i]<=8)
                                    {
                                        SBitArray[k]=BitArray[i];
                                        k++;
                                    }
                                    else
                                    {
                                        SBitArray[k]=BitArray[i];
                                        k++;
                                        SBitArray[k]=BitArray[i];
                                        k++;
                                    }
                                }
                                catch (ArrayIndexOutOfBoundsException ex)
                                {
                                    k=0;
                                    Log.d(null, "ArrayIndexOutOfBoundsException");
                                    break;

                                }

                            }
                            if(k==(72*2))
                            {
                                k=0;
                                isDataTrue=true;
                                for(int i=0;i<72;i++)
                                {
                                    if(SBitArray[k]==0&&SBitArray[k+1]==1)
                                    {
                                        mqstDataBit[i]=1;
                                    }
                                    else if(SBitArray[k]==1&&SBitArray[k+1]==0)
                                    {
                                        mqstDataBit[i]=0;
                                    }
                                    else
                                    {
                                        isDataTrue=false;
                                    }
                                    k+=2;
                                }
                                if(isDataTrue)
                                {
                                    _8993Vel=0;
                                    _8993Rh=0;
                                    _8993Tair=0;
                                    _8993Bat=0;

                                    int dataIndex=8;
                                    for(int i=0;i<16;i++)
                                    {
                                        _8993Vel+=(mqstDataBit[dataIndex]<<(15-i));
                                        dataIndex++;
                                    }
                                    //dataIndex+=16;
                                    for(int i=0;i<16;i++)
                                    {
                                        _8993Rh+=(mqstDataBit[dataIndex]<<(15-i));
                                        dataIndex++;
                                    }

                                    //dataIndex+=16;
                                    for(int i=0;i<16;i++)
                                    {
                                        _8993Tair+=(mqstDataBit[dataIndex]<<(15-i));
                                        dataIndex++;
                                    }
                                    //dataIndex+=16;
                                    for(int i=0;i<16;i++)
                                    {
                                        _8993Bat+=(mqstDataBit[dataIndex]<<(15-i));
                                        dataIndex++;
                                    }



                                }
                            }
                        }
                        //if(isDataTrue)
                        //short[] SBitTimeArray=new short[72*2];



                    }
                }
            }
        }
    }


    public void msg_IC_num(String str){//发送系统消息

    }

}

