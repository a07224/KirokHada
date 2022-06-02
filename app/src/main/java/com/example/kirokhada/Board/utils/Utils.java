package com.example.kirokhada.Board.utils;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

    // 숫자인지 여부 체크
    public static boolean isNumber(String str){
        try {
            Double.parseDouble(str) ;
            return true;
        } catch(Exception e){
            return false;
        }
    }

}

