/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package searchengine;

import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author ahmos
 */
public class Producer implements Runnable {

    private Carawler carawler;

    public Producer(Carawler carawler) {
        this.carawler = carawler;
    }

    public Carawler getCarawler() {
        return carawler;
    }

    public void setCarawler(Carawler carawler) {
        this.carawler = carawler;
    }
    boolean checkdomain(String url)
    {
    
    try {



          URL  verifiedUrl = new URL(url);

           String host = verifiedUrl.getHost();

            if (!carawler.getDomaindepth().containsKey(host)) {

                carawler.domaindepth.put(host, 1);

                return true;

            }

            int count = carawler.getDomaindepth().get(host);

            //verfiy for domain tree



            if (count < 20) {

                count++;

                carawler.getDomaindepth().replace(host, count);

                return true;

            } else if (count >= 20 && carawler.getPagesToVisit().size() >1000) {

                return false;

            }



        } catch (Exception e) {

            return false;

        }



        return true;

    }
    
    

    public void search() {

        while (true) {
            webPage currentUrl = null;
            Consumer consumer = new Consumer("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0", this);
            String host = "";
            synchronized (carawler) {

                while (this.carawler.getPagesToVisit().isEmpty()) {

                    try {
                        // if no urls in the tovisit so we have to wait 
                        carawler.wait();
                    } catch (InterruptedException ex) {
                        //Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            synchronized (carawler) {
                // getting the next link to carawl 
                currentUrl = this.nextUrl(host);

                // check for the url is accepted by the robot
                Robot robot = new Robot();
                if (currentUrl == null || !robot.isSafeUrl(currentUrl.Url, "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0", carawler)) {
                    continue;

                }
                // add the link we get to the page visited 
                this.carawler.getPagesVisited().add(currentUrl);
                System.out.println(Thread.currentThread().getName() + " add a link to pagesVisited " + currentUrl.getUrl() + "now it have size" + carawler.getPagesVisited().size());
            }

            System.out.println("the link now is " + currentUrl.getUrl());
            // starting the recarwl 
            boolean add = consumer.Start(currentUrl);

            synchronized (carawler) {
                // if url return 4xx add it in disallowed urls 
                // or wrong portocol 
                if (add == false) {
                    this.carawler.getPagesVisited().remove(currentUrl);
                    // it will be added in Robotstxt so we didnt visit it again 
                    if (carawler.getRobotTxtFiles().get(host) != null) {
                        carawler.getRobotTxtFiles().get(host).add(currentUrl.Url);
                    } else {

                        // adding the links of robot text to RObottxtfiles list
                        ArrayList disallowList = new ArrayList();
                        disallowList.add(currentUrl.Url);
                        if (host != null) {
                            carawler.getRobotTxtFiles().put(host, disallowList);
                        }
                    }
                }
                if (consumer.getpage() == null) {
                    carawler.getPagesVisited().remove(currentUrl);
                    continue;
                }
                if (carawler.getPagesVisited().size() >= carawler.getNumberOfPages()) {
                    System.out.println(Thread.currentThread().getName() + "   has finished it's work with page size = " + this.carawler.getPagesVisited().size());
                    carawler.notifyAll();
                    return;
                }
                System.out.println(Thread.currentThread().getName() + " add a link to pages to visit  " + currentUrl.getUrl() + "now it have size" + consumer.getLinks().size());

                if (consumer.getLinks() != null && carawler.getPagesToVisit().size() + consumer.getLinks().size() < 20000) {
                    this.carawler.getPagesToVisit().addAll(consumer.getLinks());

                    currentUrl.getChildPages().addAll(consumer.getLinks());
                }
                if (consumer.getpage() != null) /*    if(!carawler.getPages().contains( consumer.getpage()))
                          this.carawler.getPages().add(consumer.getpage());
                 */ {
                    currentUrl.setPage(consumer.getpage());
                }

                carawler.notifyAll();
            }

        }

    }

    private webPage nextUrl(String host) {
        webPage nextUrl;

        do {
            if (this.carawler.getPagesToVisit().isEmpty()) {
                return null;
            }
            nextUrl = this.carawler.getPagesToVisit().iterator().next();
            this.carawler.getPagesToVisit().remove(nextUrl);
            // this.carawler.getPagesToVisit().remove(0);
            System.out.println("The link is searching in " + nextUrl.Url + "the number of to visit list" + carawler.getPagesToVisit().size());
        } while (this.carawler.isInVisited(nextUrl) || !checkdomain(nextUrl.getUrl())); // to not visit a website more than one time 

        return nextUrl;
    }

    @Override
    public void run() {

        search();
    }
}
