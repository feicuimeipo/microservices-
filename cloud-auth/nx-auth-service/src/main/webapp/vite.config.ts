import { fileURLToPath, URL } from "node:url";

import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";

import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";

import { ElementPlusResolver } from "unplugin-vue-components/resolvers";
import Unocss from "unocss/vite";
import {
  presetAttributify,
  presetIcons,
  presetUno,
  transformerDirectives,
  transformerVariantGroup,
} from "unocss";

//https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, __dirname); // loadEnv加载__dirname目录下的 mode文件，若mode为'dev',则加载.env.dev。
  //const rootPath = process.cwd();
  //console.log("env=", mode, "baseUrl=", env.VITE_APP_BASE_URL);
  return {
    base: env.VITE_APP_BASE_URL, // 静态资源基础路径 base: './' || '',
    //base: "./",
    resolve: {
      alias: {
        "@": fileURLToPath(new URL("./src", import.meta.url)),
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/styles/element/index.scss" as *;`,
        },
      },
    },
    plugins: [
      vue(),
      vueJsx(),
      AutoImport({
        resolvers: [ElementPlusResolver()],
        dts: "src/auto-imports.d.ts",
      }),
      Components({
        // allow auto load markdown components under `./src/components/`
        extensions: ["vue", "md"],
        // allow auto import and register components used in markdown
        include: [/\.vue$/, /\.vue\?vue/, /\.md$/],
        resolvers: [
          ElementPlusResolver({
            importStyle: "sass",
          }),
        ],
        dts: "src/components.d.ts",
      }),
      // see unocss.config.ts for config
      Unocss({
        presets: [
          presetUno(),
          presetAttributify(),
          presetIcons({
            scale: 1.2,
            warn: true,
          }),
        ],
        transformers: [transformerDirectives(), transformerVariantGroup()],
      }),
    ],
    // https://github.com/antfu/unocss
    // see unocss.config.ts for config
    server: {
      host: "0.0.0.0",
      port: 1080,
      open: false,
      proxy: {
        "/api": {
          target: env.VITE_APP_BASE_API, //跨域访问的网址
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ""),
        },
      },
    },
    build: {
      sourcemap: false,
      minify: "terser",
      terserOptions: {
        compress: {
          drop_console: true,
          drop_debugger: true,
        },
      },
      chunkSizeWarningLimit: 1500,
      //emptyOutDir: true,
      rollupOptions: {
        input: "index.html",
        output: {
          manualChunks(id) {
            if (id.includes("node_modules")) {
              const arr = id.toString().split("node_modules/")[1].split("/");
              switch (arr[0]) {
                case "@naturefw": // 自然框架
                case "@popperjs":
                case "@vue":
                case "element-plus": // UI 库
                case "@element-plus": // 图标
                  return "_" + arr[0];
                  break;
                default:
                  return "__vendor";
                  break;
              }
              //[0].toString();
            }
          }, //manualChunks
          chunkFileNames: (chunkInfo) => {
            const facadeModuleId = chunkInfo.facadeModuleId
              ? chunkInfo.facadeModuleId.split("/")
              : [];
            const fileName =
              facadeModuleId[facadeModuleId.length - 2] || "[name]";
            return `js/${fileName}/[name].[hash].js`;
          },
        },
      }, //rollupOptions
    },
  }; //return
});
