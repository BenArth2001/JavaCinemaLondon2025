package util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageUtils {
    private static final String IMAGE_DIR = "out/production/CinemaJavaPro/images";
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_HEIGHT = 180;

    /**
     * Load an image from the given path in the images directory
     * 
     * @param imagePath The relative path to the image
     * @return ImageIcon object or null if image cannot be loaded
     */
    public static ImageIcon loadImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return getPlaceholderImage();
        }

        try {
            // Check if the path is absolute or relative
            File imageFile;
            if (imagePath.contains("/") || imagePath.contains("\\")) {
                imageFile = new File(imagePath);
            } else {
                imageFile = new File(IMAGE_DIR, imagePath);
            }

            if (!imageFile.exists()) {
                System.out.println("Image not found: " + imageFile.getAbsolutePath());
                return getPlaceholderImage();
            }

            BufferedImage img = ImageIO.read(imageFile);
            return new ImageIcon(img);
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            return getPlaceholderImage();
        }
    }

    /**
     * Get a scaled version of the image
     * 
     * @param image  The original ImageIcon
     * @param width  Target width
     * @param height Target height
     * @return Scaled ImageIcon
     */
    public static ImageIcon getScaledImage(ImageIcon image, int width, int height) {
        if (image == null) {
            return getPlaceholderImage(width, height);
        }

        Image img = image.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    /**
     * Get a scaled version of the image with default dimensions
     * 
     * @param image The original ImageIcon
     * @return Scaled ImageIcon
     */
    public static ImageIcon getScaledImage(ImageIcon image) {
        return getScaledImage(image, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Load and scale an image directly from path
     * 
     * @param imagePath The path to the image
     * @param width     Target width
     * @param height    Target height
     * @return Scaled ImageIcon
     */
    public static ImageIcon getScaledImage(String imagePath, int width, int height) {
        ImageIcon icon = loadImage(imagePath);
        return getScaledImage(icon, width, height);
    }

    /**
     * Load and scale an image with default dimensions
     * 
     * @param imagePath The path to the image
     * @return Scaled ImageIcon
     */
    public static ImageIcon getScaledImage(String imagePath) {
        return getScaledImage(imagePath, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Create a placeholder image for when a movie has no poster
     * 
     * @return Placeholder ImageIcon
     */
    public static ImageIcon getPlaceholderImage() {
        return getPlaceholderImage(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Create a placeholder image with specific dimensions
     * 
     * @param width  Width of the placeholder
     * @param height Height of the placeholder
     * @return Placeholder ImageIcon
     */
    public static ImageIcon getPlaceholderImage(int width, int height) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();

        // Fill background
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, width, height);

        // Draw border
        g2d.setColor(Color.GRAY);
        g2d.drawRect(0, 0, width - 1, height - 1);

        // Draw "No Image" text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "No Image";
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        g2d.drawString(text, (width - textWidth) / 2, height / 2 + textHeight / 4);

        g2d.dispose();
        return new ImageIcon(placeholder);
    }

    /**
     * Ensure the images directory exists
     */
    public static void ensureImageDirectoryExists() {
        File imageDir = new File(IMAGE_DIR);
        if (!imageDir.exists()) {
            boolean created = imageDir.mkdirs();
            if (created) {
                System.out.println("Created image directory: " + imageDir.getAbsolutePath());
            } else {
                System.err.println("Failed to create image directory: " + imageDir.getAbsolutePath());
            }
        }
    }
}
