package network.commonClass;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图片操作类
 * @author ZiQin
 * @version v1.1.0
 * 【添加】图片切圆功能
 * @version v1.0.0
 */
public class Picture {

    /**
     * 图片缓冲区
     */
    private BufferedImage picture;

    /**
     * 图片大小（默认jpg格式编码大小）
     */
    private int pictureSize;

    /**
     * 默认构造函数
     */
    public Picture() {
        pictureSize = 0;
        picture = null;
    }

    /**
     * 构造函数
     * @param pictureStream 图片字节流
     */
    public Picture(byte[] pictureStream) {
        this.picture = bytes2Picture(pictureStream);
        pictureSize = pictureStream.length;
    }

    /**
     * 构造函数
     * @param filePath 文件路径
     * @throws IOException 文件访问失败
     */
    public Picture(String filePath) throws IOException {
        picture = ImageIO.read(new File(filePath));
        pictureSize = picture2Bytes(picture, "jpg").length;
    }

    /**
     * 从文件中读取图片
     * @param filePath 文件路径
     * @return 读取成功或失败
     */
    public boolean readPicture(String filePath) {
        try {
            picture = ImageIO.read(new File(filePath));
            pictureSize = picture2Bytes(picture, "jpg").length;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取图片字节流
     * @param pictureStream 图片字节流
     */
    public void readPicture(byte[] pictureStream) {
        this.picture = bytes2Picture(pictureStream);
        pictureSize = pictureStream.length;
    }

    /**
     * 保存图片到文件
     * @param filePath 文件路径
     * @param saveFormat 图片保存格式
     * @return 保存成功或失败
     */
    public boolean savePicture(String filePath, String saveFormat) {
        try {
            ImageIO.write(picture, saveFormat, new File(filePath));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 重置图片大小
     * @param width     重新设置图片的宽
     * @param height    重新设置图片的高
     */
    public void reduceImage(int width, int height) {
        // 缩小边长
        BufferedImage newPicture = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 绘制 缩小  后的图片
        newPicture.getGraphics().drawImage(picture, 0, 0, width, height, null);
        picture = newPicture;
        pictureSize = picture2Bytes(newPicture, "jpg").length;
    }

    /**
     * 按倍率缩小图片（宽、高一起等比率缩小）
     * @param ratio  缩小比率
     */
    public void reduceImage(int ratio) {
        int width = picture.getWidth();
        int height = picture.getHeight();
        // 缩小边长
        BufferedImage newPicture = new BufferedImage(width / ratio, height / ratio, BufferedImage.TYPE_INT_RGB);
        // 绘制 缩小  后的图片
        newPicture.getGraphics().drawImage(picture, 0, 0, width / ratio, height / ratio, null);
        picture = newPicture;
        pictureSize = picture2Bytes(newPicture, "jpg").length;
    }
    
    /**
     * 将图片剪裁成圆形的
     */
    public void reduceImageToCircle() {
    	// BufferedImage bi1 = ImageIO.read(new File("image/02.png"));	// 通过文件名打开文件
    	        BufferedImage bi2 = new BufferedImage(picture.getWidth(), 
    	        			picture.getHeight(),BufferedImage.TYPE_INT_RGB);// 根据需要是否使用 BufferedImage.TYPE_INT_ARGB
    	        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, picture.getWidth(), picture.getHeight());
    	        Graphics2D g2 = bi2.createGraphics();
    	        g2.setBackground(Color.WHITE);
    	        g2.fill(new Rectangle(bi2.getWidth(), bi2.getHeight()));
    	        g2.setClip(shape);
    	        // 使用 setRenderingHint 设置抗锯齿
    	        g2.drawImage(picture, 0, 0, null);
    	        g2.dispose();
    	        picture = bi2;
    	        pictureSize = this.picture2Bytes(bi2, "jpg").length;
    }

    /**
     * 获取图片字节流
     * @return 图片字节流
     */
    public byte[] getPictureBytes() {
        return picture2Bytes(picture, "jpg");
    }

    /**
     * 获取图片大小（字节）
     * @return 图片大小
     */
    public int getPictureSize() {
        return pictureSize;
    }

    /**
     * 克隆对象本身（默认jpg格式）
     * @return 新的对象
     */
    public Picture clone() {
        return new Picture(this.getPictureBytes());
    }

    /**
     * 图片转字节流形式（默认Jpg格式）
     * @param bufferedImage 要转换的图片缓冲对象
     * @return
     */
    private byte[] picture2Bytes(BufferedImage bufferedImage, String format) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, format, byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 字节流（默认Jpg格式）转图片
     * @param pictureBytes
     * @return
     */
    private BufferedImage bytes2Picture(byte[] pictureBytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pictureBytes);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(byteArrayInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return bufferedImage;
        }
    }
}
