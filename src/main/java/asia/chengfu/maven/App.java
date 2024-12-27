package asia.chengfu.maven;

import asia.chengfu.line.*;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Commandline commandline = new Commandline(new MavenDownloadMenuService());
        commandline.start();
    }

    private static long downloadJar(String storagePath, MavenJar mavenJar) {
        // https://repo1.maven.org/maven2/com/aliyun/tea-util/0.2.21/tea-util-0.2.21.jar
        String repositoryAddress = "https://repo1.maven.org/maven2/";
        String downloadUrl = repositoryAddress + getUri(mavenJar);
        String fileName = mavenJar.getArtifactId() + "-" + mavenJar.getVersion() + "." + mavenJar.getPackaging();
        return HttpUtil.downloadFile(downloadUrl + fileName, storagePath);
    }


    private static String getUri(MavenJar mavenJar) {
        return mavenJar.getGroupId().replace(".", "/") +
                "/" + mavenJar.getArtifactId() +
                "/" + mavenJar.getVersion() + "/";
    }

    private static String autoCreatePath(MavenJar mavenJar) {
        String root = "~/.m2/repository/" + getUri(mavenJar);
        FileUtil.mkdir(root);
        return root;
    }

    private static class MavenJar {
        private final String groupId;
        private final String artifactId;
        private final String version;
        private final String packaging;

        public MavenJar(String groupId, String artifactId, String version, String packaging) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.packaging = packaging;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public String getVersion() {
            return version;
        }

        public String getPackaging() {
            return packaging;
        }
    }

    public static class MavenDownloadMenuService implements IMenuService {

        @Override
        public List<Menu> listMenus() {
            return ListUtil.of(
                    Menu.create("交互式jar下载", new IProcessor() {
                        @Override
                        public void process(Map<Key, Object> map) {
                            Console.print("请输入groupId：");
                            String groupId = Console.input();

                            Console.print("请输入artifactId：");
                            String artifactId = Console.input();

                            Console.print("请输入version：");
                            String version = Console.input();

                            Console.print("请输入packaging（jar）：");
                            String packaging = StrUtil.blankToDefault(Console.input(), "jar");

                            MavenJar mavenJar = new MavenJar(groupId, artifactId, version, packaging);
                            download(mavenJar);
                        }
                    }),
                    Menu.create("通过FSQN下载（com.aliyun:endpoint-util:jar:0.0.7）", new IProcessor() {
                        @Override
                        public void process(Map<Key, Object> map) {
                            Console.print("请输入fsqn：");
                            String fsqn = Console.input();

                            //com.aliyun:endpoint-util:jar:0.0.7
                            String[] split = fsqn.split(":");

                            String groupId = split[0];
                            String artifactId = split[1];
                            String version = split[3];
                            String packaging = split[2];

                            MavenJar mavenJar = new MavenJar(groupId, artifactId, version, packaging);
                            download(mavenJar);
                        }
                    })
            );
        }
    }

    static void download(MavenJar mavenJar) {
        String storagePath = autoCreatePath(mavenJar);
        long fileSize = downloadJar(storagePath, mavenJar);
        Console.log("下载成功！文件大小【{}】路径【{}】 ", fileSize, storagePath);
    }

}
