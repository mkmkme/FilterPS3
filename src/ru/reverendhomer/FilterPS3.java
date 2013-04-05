package ru.reverendhomer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FilterPS3 {
	Color[][] pixels;
	int height, width, blur;
	
	public FilterPS3(BufferedImage image, int blur) {
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.pixels = new Color[width][height];
		this.blur = blur;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				pixels[i][j] = new Color(image.getRGB(i, j));
			}
		}
	}
	
	public void blur() {
		int _red, _green, _blue;
		for (int step = 0; step < blur; step++) {
			for (int _width = 0; _width < width; _width++) {
				for (int _height = 0; _height < height; _height++) {
					_red = _green = _blue = 0;
					for (int x = Math.max(0, _width - 4); x < Math.min(width, _width + 4); x++) {
						for (int y = Math.max(0, _height - 4); y < Math.min(height, _height + 4); y++) {
							_red += pixels[x][y].getRed();
							_green += pixels[x][y].getGreen();
							_blue += pixels[x][y].getBlue();
						}
					}
					_red = (int)(_red / 64);
					_green = (int)(_green / 64);
					_blue = (int)(_blue / 64);
					pixels[_width][_height] = new Color(_red, _green, _blue);
				}
			}
		}
	}
	
	public void makeCover() throws IOException {
		blur();
		if (width > height) addPS3Horizontal();
		else addPS3Vertical();
	}
	
	public void addPS3Horizontal() throws IOException {
		File f = new File("PS3Horizontal.jpg");
		BufferedImage ps3 = ImageIO.read(f);
		int ps3width = ps3.getWidth();
		int ps3height = ps3.getHeight();
		double step = (double)ps3width / (double)width;
		for (int _height = 0; _height < ps3height; _height++) {
			double _x = 0;
			for (int _width = 0; _width < width; _width++) {
				pixels[_width][_height] = new Color(ps3.getRGB((int)_x, _height));
				_x = Math.min(ps3width - 1, _x + step);
			}
		}
	}
	
	public void addPS3Vertical() throws IOException {
		File f = new File("PS3Vertical.jpg");
		BufferedImage ps3 = ImageIO.read(f);
		int ps3width = ps3.getWidth();
		int ps3height = ps3.getHeight();
		double step = (double)ps3height / (double)height;
		for (int _width = 0; _width < ps3width; _width++) {
			double _x = 0;
			for (int _height = 0; _height < height; _height++) {
				pixels[_width][_height] = new Color(ps3.getRGB(_width, (int)_x));
				_x = Math.min(_x + step, ps3height - 1);
			}
		}
	}
	
}
