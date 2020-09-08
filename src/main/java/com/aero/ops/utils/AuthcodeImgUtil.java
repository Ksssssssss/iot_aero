package com.aero.ops.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class AuthcodeImgUtil {
    public static char[] getAuthCode(char[] chr1) {
        char[] chr = "0123456789abcdefhijkmnpqrstuvwxy".toCharArray();
//        char[] chr = "0123456789".toCharArray();
        Random r = new Random();
        int n = chr.length;
        boolean[] used = new boolean[n];
        int j = 0;
        while (true) {
            int index = r.nextInt(n);
            if (used[index] == true)
                continue;
            chr1[j] = chr[index];
            used[index] = true;
            j++;
            if (j >= 4)
                break;
        }
        return chr1;

    }

    public static Color getRandColor(int fc, int bc) {// 给定范围获得随机颜色
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * @param args
     * @throws IOException
     */
    public static BufferedImage getIMG(String authCode) throws IOException {
        int width = 120, height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);

        g.setFont(new Font("Times New Roman", Font.PLAIN, 32));

        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }
        char[] arr = new char[4];// 生成四位编码
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.drawString(authCode.charAt(i) + "", 26 * i + 12, 26);
        }
        return image;
    }
}

