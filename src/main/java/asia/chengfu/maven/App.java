package asia.chengfu.maven;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpUtil;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        Console.print("请输入groupId：");
        String groupId = Console.input();

        Console.print("请输入artifactId：");
        String artifactId = Console.input();

        Console.print("请输入version：");
        String version = Console.input();

//        Console.print("请输入packaging：");
//        String packaging = Console.input();

        Console.print("请输入下载路径：");
        String downloadPath = Console.input();

        String storagePath = autoCreateDir(groupId, artifactId, version);

        long fileSize = downloadJar(storagePath, downloadPath);

        Console.log("下载成功！文件大小【{}】路径【{}/{}】 ", fileSize, storagePath, FileUtil.mainName(downloadPath));
    }

    private static long downloadJar(String storagePath, String downloadPath) {
        long fileSize = HttpUtil.downloadFile(downloadPath, storagePath);
        return fileSize;
    }


    private static String autoCreateDir(String groupId, String artifactId, String version){
        String root = "~/.m2/repository/";
        String path = root + groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/";
        FileUtil.mkdir(path);
        return path;
    }
}
