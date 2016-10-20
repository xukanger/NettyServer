package com.just.server.http.bootstrap;

import com.just.server.http.http.HttpStaticFileServer;
import com.just.server.http.https.HttpsStaticFileServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yt on 2016/10/19.
 */
public class Bootstrap {

    public static void main(String[]args){
        ExecutorService executorService= Executors.newFixedThreadPool(2);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpStaticFileServer.main(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpsStaticFileServer.main(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try{
            while(!executorService.isShutdown()){
                Thread.yield();
            }
        }
        finally {
            executorService.shutdownNow();
        }


    }

}
