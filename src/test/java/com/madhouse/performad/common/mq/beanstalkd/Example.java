package com.madhouse.performad.common.mq.beanstalkd;

import com.trendrr.beanstalk.BeanstalkClient;
import com.trendrr.beanstalk.BeanstalkException;
import com.trendrr.beanstalk.BeanstalkJob;
import com.trendrr.beanstalk.BeanstalkPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by oneal on 15/8/30.
 */
public class Example {

//    private static String HOST = "localhost";
    private static String HOST = "172.16.30.47";

    protected static final Log log = LogFactory.getLog(Example.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        //Example usage for a

        try {
            multipleThreadHandling();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

    /**
     * Example for using an unpooled client
     * @throws BeanstalkException
     */
    public static void clientExample() throws BeanstalkException {
        BeanstalkClient client = new BeanstalkClient(HOST, 11300, "example");
        log.info("Putting a job");
        client.put(1l, 0, 5000, "this is some data".getBytes());
        BeanstalkJob job = client.reserve(60);
        log.info("GOt job: " + job);
        client.deleteJob(job);
        client.close(); //closes the connection
    }


    public static void pooledExample()  throws BeanstalkException {
        BeanstalkPool pool = new BeanstalkPool(HOST, 11300,
                30, //poolsize
                "example" //tube to use
        );

        BeanstalkClient client = pool.getClient();

        log.info("Putting a job");
        client.put(1l, 0, 5000, "this is some data".getBytes());
        BeanstalkJob job = client.reserve(60);
        log.info("GOt job: " + job);
        client.deleteJob(job);
        client.close();  //returns the connection to the pool
    }

    public static void multipleThreadHandling() throws Exception {
        final BeanstalkPool pool = new BeanstalkPool(HOST, 11300,
                30, //poolsize
                "example" //tube to use
        );

        int totalCount  = 10000;
        final CountDownLatch latch = new CountDownLatch(totalCount);
        log.info("Putting a job");
        ExecutorService publishService = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 100; i++) {
            publishService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        BeanstalkClient client = pool.getClient();
                        for (int i = 0; i < 100; i++) {
                            client.put(1l, 0, 5000, ("this is some data: " + i).getBytes());
                            latch.countDown();
                        }
                        client.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final CountDownLatch exitLatch = new CountDownLatch(totalCount);

        // handle the message
        Date start = new Date();

        ExecutorService messageHandler = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 10000; i++) {
            messageHandler.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        BeanstalkClient client = pool.getClient();
                        BeanstalkJob job = client.reserve(60);
                        client.deleteJob(job);
                        client.close();  //returns the connection to the pool
                        exitLatch.countDown();
                    } catch (BeanstalkException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        log.info("waiting to exit");
        exitLatch.await();
        Date end = new Date();
        System.out.println("time used: " + (end.getTime() - start.getTime()));
    }
}
