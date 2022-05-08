package com.example.demo;

import java.util.TreeSet;

public class DemoTest {

    public static void main(String[] args) {
        TreeSet<String> strings = new TreeSet<>();

        strings.add("abc");
        strings.forEach(item -> {
            System.out.println(item);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("------------");
    }
}
