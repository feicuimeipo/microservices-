/// <reference types="vite/client" />

//import {DefineComponent} from "vue";

interface ImportMetaEnv {
  readonly NODE_ENV: string;
  readonly NODE_ENV: string;
  readonly VITE_APP_BASE_URL: string;
  readonly VITE_APP_BASE_API: string;
  readonly VITE_APP_ENV: string;
  readonly VITE_APP_TITLE: string;
  readonly VITE_APP_REMEMBER_ME_MAX_AGE:number
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

declare module "*.vue" {
  import { DefineComponent } from "vue";
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>;
  export default component;
}
