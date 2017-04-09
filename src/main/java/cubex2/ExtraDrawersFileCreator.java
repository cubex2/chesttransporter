package cubex2;

import com.google.common.collect.Maps;
import org.apache.commons.io.Charsets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExtraDrawersFileCreator
{
    private static final File base = new File("textures");

    private static int[] full1;
    private static int[] full2;
    private static int[] full4;

    public static void main(String[] args) throws IOException
    {
        new File(base, "models").mkdir();

        full1 = getTemplateData("template_full1");
        full2 = getTemplateData("template_full2");
        full4 = getTemplateData("template_full4");

        createTextures("forestry");
        createTextures("natura");
        createTextures("biomesoplenty");
        createTextures("immersiveengineering");

        createModels("forestry");
        createModels("natura");
        createModels("biomesoplenty");
        createModels("immersiveengineering");

        List<String> forestryNames = getWoodNames("forestry").stream().map(s -> "\"forestry_" + s + "\"").collect(Collectors.toList());
        List<String> naturaNames = getWoodNames("natura").stream().map(s -> "\"natura_" + s + "\"").collect(Collectors.toList());
        List<String> bopNames = getWoodNames("biomesoplenty").stream().map(s -> "\"biomesoplenty_" + s + "\"").collect(Collectors.toList());
        List<String> ieNames = getWoodNames("immersiveengineering").stream().map(s -> "\"immersiveengineering_" + s + "\"").collect(Collectors.toList());

        System.out.println(forestryNames);
        System.out.println(naturaNames);
        System.out.println(bopNames);
        System.out.println(ieNames);
    }

    private static void createModels(String dir) throws IOException
    {
        String[] variants = new String[] {"full1", "full2", "full4", "half2", "half4"};

        List<String> names = getWoodNames(dir);

        for (String name : names)
        {
            for (String variant : variants)
            {
                String model = getModelContent(dir, "drawers_" + name + "_" + variant.replaceAll("[^0-9]", ""));
                File file = new File(base, "models/extra_drawer_" + variant + "_" + dir + "_" + name + ".json");

                Files.write(file.toPath(), model.getBytes(Charsets.UTF_8), StandardOpenOption.CREATE);
            }
        }
    }

    private static String getModelContent(String dir, String textureName)
    {
        return "{\n" +
               "  \"parent\": \"item/generated\",\n" +
               "  \"textures\": {\n" +
               "    \"layer0\": \"chesttransporter:items/" + dir + "_" + textureName + "\"\n" +
               "  }\n" +
               "}";
    }

    private static List<String> getWoodNames(String dir)
    {
        File folder = new File(base, dir);
        File[] files = folder.listFiles();

        return Arrays.stream(files)
                     .map(f -> f.getName()
                                .replace("drawers_", "")
                                .replace("_front_1.png", "")
                                .replace("_front_2.png", "")
                                .replace("_front_4.png", ""))
                     .distinct()
                     .collect(Collectors.toList());
    }

    private static void createTextures(String dir)
    {
        List<Image> originals = loadOriginals(dir);

        File forestry_out = new File(base, "out");
        if (forestry_out.exists())
            forestry_out.delete();

        forestry_out.mkdir();

        for (Image original : originals)
        {
            BufferedImage tex1 = createTexture(getTemplate(original.name), createMapper(original.name, original.img));
            writeImage(tex1, new File(forestry_out, dir + "_" + original.name.replace("_front", "") + ".png"));
        }
    }

    private static int[] getTemplate(String name)
    {
        if (name.contains("_1"))
            return full1;
        if (name.contains("_2"))
            return full2;
        return full4;
    }

    private static Function<Integer, Integer> createMapper(String name, BufferedImage img)
    {
        Map<Integer, Integer> colorMap = Maps.newHashMap();
        colorMap.put(0, 0); // alpha
        colorMap.put(2, img.getRGB(4, 15)); // black
        colorMap.put(3, img.getRGB(2, 13)); // red

        if (name.contains("_1"))
        {
            colorMap.put(1, img.getRGB(7, 1)); // white
        } else if (name.contains("_2"))
        {
            colorMap.put(1, img.getRGB(7, 1)); // white
        } else
        {
            colorMap.put(1, img.getRGB(3, 9)); // white
        }

        return colorMap::get;
    }

    private static void writeImage(BufferedImage img, File file)
    {
        try
        {
            ImageIO.write(img, "png", file);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static BufferedImage createTexture(int[] template, Function<Integer, Integer> templateMapper)
    {
        BufferedImage ret = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                ret.setRGB(x, y, templateMapper.apply(template[x + y * 16]));
            }
        }

        return ret;
    }

    private static int[] getTemplateData(String name)
    {
        Image image = new Image(name);
        BufferedImage img = image.img;
        int[] rgb = img.getRGB(0, 0, 16, 16, null, 0, 16);

        rgb = Arrays.stream(rgb)
                    .map(c ->
                         {
                             if (c == 0xffffffff)
                                 return 1;
                             if (c == 0xff000000)
                                 return 2;
                             if (c == 0xffff0000)
                                 return 3;
                             return 0;
                         })
                    .toArray();

        return rgb;
    }

    private static List<Image> loadOriginals(String dir)
    {
        File folder = new File(base, dir);
        File[] files = folder.listFiles();

        List<Image> ret = Arrays.stream(files)
                                .map(f -> new Image(dir + "/" + f.getName().replace(".png", "")))
                                .collect(Collectors.toList());

        return ret;
    }

    private static class Image
    {
        private String name;
        private BufferedImage img;

        public Image(String name)
        {
            this.name = name.substring(name.indexOf('/') + 1);

            try
            {
                img = ImageIO.read(new File(base, name + ".png"));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
