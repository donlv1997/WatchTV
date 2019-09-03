package cn.nodemedia.demo;

import java.util.Random;

public class Item {
    private String url;
    private String name;
    private String blank = "";//随机1-4个换行符
    private int imageId;//R.drawable.apple_pic形式的id是用int存储的

    public Item(String name,String url ,int imageId) {
        this.name = name;
        this.url = url;
        this.imageId = imageId;
        Random random = new Random();
        int length = random.nextInt(3) + 1;//用random创造了1-4之间的随机数
        for (int i = 0; i < length; i++) {
            blank += "\n";
        }
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getBlank() {
        return blank;
    }

    public int getImageId() {
        return imageId;
    }
}