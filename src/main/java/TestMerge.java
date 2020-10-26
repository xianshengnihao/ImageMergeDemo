import com.sun.istack.internal.NotNull;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestMerge {
    private static Map<String, String> ymlByFileName;
    public static void main(String[] args) {
        Map<String,String> textMap = new HashMap<String, String>();
            textMap.put("transSeqNo","1234567890");
            textMap.put("businessType","实名认证");
            textMap.put("businessTime","2020.10.18");
            textMap.put("IDType","身份证");
            textMap.put("userName","陈平安");
            textMap.put("birthDate.year","1995");
            textMap.put("birthDate.month","11");
            textMap.put("birthDate.day","11");
            textMap.put("IDNo","513721199508250220");
            textMap.put("issueOffice","成都市公安局青羊区分局");
            textMap.put("expireDate","2030.08.25");
            textMap.put("photoCompareID","85");
            textMap.put("photoCompareOnline","95");
            textMap.put("transChnl","测试业务渠道");
            textMap.put("businesResult","成功");
            textMap.put("authenChnl","测试鉴权渠道");
            textMap.put("authenResult","成功");
            textMap.put("userAddr","四川省成都市锦江区");

        Map<String,String> imageMap = new HashMap<String, String>();
            imageMap.put("IDFront","E:\\ImageMergeDemo\\image\\身份证正面.jpg");
            imageMap.put("IDObverse","E:\\ImageMergeDemo\\image\\身份证反面.jpg");
            imageMap.put("retiImage","E:\\ImageMergeDemo\\image\\身份证正面.jpg");
            imageMap.put("siteCollect","E:\\ImageMergeDemo\\image\\证件采集.jpg");
            imageMap.put("IDCollect","E:\\ImageMergeDemo\\image\\证件采集.jpg");

        String backgroundPath = "E:\\ImageMergeDemo\\image\\temp-1.jpg";
        String descPath = "E:\\ImageMergeDemo\\test1.jpg";
        String tempYmlPath = "src\\main\\resources\\temp.yml";
        String tempName = "temp_1";

        long start = System.currentTimeMillis();
        mergeImage(textMap,imageMap,backgroundPath,descPath,tempYmlPath,tempName);
        long end = System.currentTimeMillis();
        System.out.println("合并图片耗时：" + (end - start) + "ms");
    }
//    Map<String, String> textMap,Map<String, String> pathMap,
    public static void mergeImage(Map<String, String> textMap,Map<String, String> imageMap,String backgroundPath, String descPath,String tempYmlPath,String tempName) {
        ymlByFileName = YmlUtils.getYmlByFileName(tempYmlPath);
        int wSize = Integer.valueOf(ymlByFileName.get(tempName+"."+"size.w"));
        int hSize = Integer.valueOf(ymlByFileName.get(tempName+"."+"size.h"));
        System.out.println(ymlByFileName.toString());
        try {
            //读取模板背景图片并指定大小size
            Thumbnails.Builder<File> background = Thumbnails.of(backgroundPath).size(wSize, hSize);
            //将元素图片合成到模板背景图片上
            background = handleImage(imageMap, tempName,background);
            //合并后的图片转化为BufferedImage流
            BufferedImage backgroundImageBuffered = background.asBufferedImage();
            //将文字信息合成到流中并返回
            backgroundImageBuffered = handleText(textMap,backgroundImageBuffered,tempName);
            //将处理好的流输出到目标文件中
            Thumbnails.of(backgroundImageBuffered).size(wSize, hSize).toFile(descPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage handleText(Map<String, String> textMap, @NotNull BufferedImage backgroundImageBuffered, String tempName) {
        //使用背景图片流生成2d图形类
        Graphics2D g = backgroundImageBuffered.createGraphics();
        //设置使用抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //设置文字格式
        Font font = new Font("微软雅黑", Font.PLAIN, 60);
        g.setColor(Color.BLACK);
        g.setFont(font);
        //使用图形类将文字信息合并到背景图形对象中
        if (textMap!=null && textMap.size()>0 && backgroundImageBuffered!=null){
            for (Map.Entry<String,String> entry:textMap.entrySet()){
                    g.drawString(entry.getValue(),Integer.valueOf(ymlByFileName.get(tempName+"."+entry.getKey()+".x")),
                            Integer.valueOf(ymlByFileName.get(tempName+"."+entry.getKey()+".y")));
            }
        }
        return backgroundImageBuffered;
    }

    public static Thumbnails.Builder<File> handleImage(Map<String, String> imageMap,String tempName,Thumbnails.Builder<File> background) {
        Map<String, BufferedImage> bufferedImageMap = new HashMap<String, BufferedImage>();
        if (imageMap != null && imageMap.size() > 0 && background!=null) {
            try {
                //将元素图片进行大小处理后转换为BufferedImage流
                for (Map.Entry<String, String> entry : imageMap.entrySet()) {
                    BufferedImage bufferedImage = Thumbnails.of(
                            entry.getValue()).size(Integer.valueOf(ymlByFileName.get(tempName+"."+entry.getKey()+".w")),
                            Integer.valueOf(ymlByFileName.get(tempName+"."+entry.getKey()+".h"))).asBufferedImage();

                    bufferedImageMap.put(entry.getKey(), bufferedImage);
                }
                //将处理好的图片对应的流合并到模板背景图片流中
                if (bufferedImageMap!=null && bufferedImageMap.size()>0){
                    for (Map.Entry<String,BufferedImage> entry:bufferedImageMap.entrySet()){
                        background = background.watermark(new Coordinate(
                                Integer.valueOf(ymlByFileName.get(tempName+"."+entry.getKey()+".x")),
                                Integer.valueOf(ymlByFileName.get(tempName+"."+entry.getKey()+".y"))),
                                entry.getValue(),1f);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  background;
    }
}

