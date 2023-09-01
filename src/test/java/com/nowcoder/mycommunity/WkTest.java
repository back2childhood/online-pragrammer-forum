package com.nowcoder.mycommunity;

import java.io.IOException;

public class WkTest {
    public static void main(String[] args) {
        String cmd = "/usr/local/bin/wkhtmltoimage --quality 75  https://www.nowcoder.com /opt/project/java/mycommunity-images/1.png";
        try {
            Runtime.getRuntime().exec(cmd);
            Thread.sleep(1000);
            System.out.println("ok");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
