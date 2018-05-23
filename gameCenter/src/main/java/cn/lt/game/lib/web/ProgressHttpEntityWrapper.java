package cn.lt.game.lib.web;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ProgressHttpEntityWrapper extends HttpEntityWrapper {
	private final FileUploadProgressListener listener;
	private long length = 0;

    public ProgressHttpEntityWrapper(final HttpEntity entity,
            final FileUploadProgressListener listener) {
        super(entity);
        length = entity.getContentLength();
        this.listener = listener;
    }

    public class CountingOutputStream extends FilterOutputStream {

        private final FileUploadProgressListener mListener;
        private long transferred;
        
        private Handler handler = new Handler(Looper.getMainLooper(),new Callback() {
    		
    		@Override
    		public boolean handleMessage(Message arg0) {
    			long paramArr[] = (long[])arg0.obj;
    			if(mListener!=null&&mListener.getIsListener()){
    				mListener.transferred(paramArr[0], paramArr[1]);
    			}
    			return false;
    		}
    	});

        CountingOutputStream(OutputStream out,
                FileUploadProgressListener listener) {
            super(out);
            this.mListener = listener;
            this.transferred = 0;
        }

        @Override
        public void write(byte[] b, int off, int len)
                throws IOException {
            // NO, double-counting, as super.write(byte[], int, int)
            // delegates to write(int).
            // super.write(b, off, len);
            out.write(b, off, len);
            this.transferred += len;
            if(mListener != null){
            	handler.sendMessage(handler.obtainMessage(0, new long[]{this.transferred, length}));
            }
        }

        @Override
        public void write(final int b) throws IOException {
            out.write(b);
            this.transferred++;
            if(mListener != null){
            	handler.sendMessage(handler.obtainMessage(0, new long[]{this.transferred, length}));
            }
        }

    }
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        this.wrappedEntity.writeTo(out instanceof CountingOutputStream ? out
                : new CountingOutputStream(out, this.listener));
    }
    
    
}
