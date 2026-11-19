/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_MOCK_ONLY?: string;
  readonly VITE_USE_MOCKS?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}