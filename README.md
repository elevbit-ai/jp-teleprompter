<div align="center">

# 📱 JP Teleprompter

### Teleprompter flutuante para gravar vídeos no WhatsApp

Um app Android leve que mostra o seu roteiro **por cima da câmera do WhatsApp**.
Você **sobe o texto com o dedo**, no seu ritmo, enquanto grava — olhando direto para a câmera.

[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)](#)
[![Tamanho](https://img.shields.io/badge/APK-21%20KB-1FBF66)](#)
[![Licença](https://img.shields.io/badge/licença-MIT-4DFF88)](LICENSE)
[![Sem anúncios](https://img.shields.io/badge/sem%20anúncios-sim-25D366)](#)

**[🌐 Site & vídeo de demonstração](https://elevbit-ai.github.io/jp-teleprompter/)** · **[⬇ Baixar APK](https://github.com/elevbit-ai/jp-teleprompter/releases/latest)**

</div>

---

## ✨ O que é

Gravar um vídeo falando um texto decorado é difícil. Teleprompters tradicionais rolam sozinhos e atrapalham o ritmo natural da fala. O **JP Teleprompter** resolve isso de um jeito simples:

- Mostra o roteiro num **painel translúcido flutuante** na metade superior da tela;
- Funciona **por cima do WhatsApp** (ou de qualquer câmera), deixando o botão de gravar livre embaixo;
- **Você controla a rolagem com o dedo** — sem velocidade automática travada.

## 🎬 Como funciona

| Passo | Ação |
|------:|------|
| 1 | Escreva ou cole o roteiro no app e ajuste tamanho da letra e opacidade. |
| 2 | Toque em **Iniciar teleprompter** e conceda a permissão *“Sobrepor a outros apps”*. |
| 3 | Abra o **WhatsApp**, entre na câmera e comece a gravar o vídeo. |
| 4 | **Suba o texto com o dedo**, no seu ritmo. Encerre no **✕** do painel. |

> Veja o vídeo completo de demonstração no **[site do projeto](https://elevbit-ai.github.io/jp-teleprompter/)**.

## 🧩 Recursos

- 👆 Rolagem manual com o dedo (controle natural da leitura)
- 🪟 Overlay sobre qualquer app, incluindo a câmera do WhatsApp
- 🔠 Ajuste de tamanho da fonte e opacidade do fundo em tempo real
- ↔️ Painel arrastável (segure a barra de título)
- 🪶 **Apenas 21 KB**, sem dependências externas, sem anúncios, sem internet
- 🔒 Privado: o roteiro fica só no aparelho; nada é coletado ou enviado

## ⬇ Instalação

1. Baixe o `JP-Teleprompter-v1.0.0.apk` na página de **[Releases](https://github.com/elevbit-ai/jp-teleprompter/releases/latest)**.
2. No Android, toque no arquivo e confirme **“Instalar mesmo assim”** (fonte desconhecida).
3. Abra o app, toque em **Iniciar teleprompter** e conceda a permissão de sobreposição.

Requisitos: Android 8.0 (API 26) ou superior.

## 🔐 Permissões e por quê

| Permissão | Para quê |
|-----------|----------|
| `SYSTEM_ALERT_WINDOW` | Desenhar o painel flutuante por cima dos outros apps. |
| `FOREGROUND_SERVICE` / `..._SPECIAL_USE` | Manter o teleprompter ativo de forma estável durante a gravação. |
| `POST_NOTIFICATIONS` | Mostrar a notificação de que o teleprompter está ativo (Android 13+). |

O app **não** usa internet, câmera, microfone, contatos nem armazenamento externo.

## 🛠️ Compilar do código-fonte

Requisitos: **JDK 17** e **Android SDK 34**.

```bash
git clone https://github.com/elevbit-ai/jp-teleprompter.git
cd jp-teleprompter
./gradlew assembleRelease     # no Windows: gradlew.bat assembleRelease
```

O APK sai em `app/build/outputs/apk/release/`. Para assinar com a sua própria chave, crie um `keystore.properties` na raiz:

```properties
storeFile=sua.keystore
storePassword=...
keyAlias=...
keyPassword=...
```

Sem esse arquivo, o build usa a chave de depuração automaticamente.

## 📂 Estrutura

```
app/src/main/java/ai/elevbit/jpteleprompter/
  ├─ MainActivity.java     # tela do roteiro e ajustes
  └─ OverlayService.java   # painel flutuante (overlay) + foreground service
app/src/main/res/          # layouts, textos (pt-BR), ícone adaptativo
docs/                      # site (GitHub Pages) + vídeo de demonstração
```

## 👤 Autor

**Joaquim Pedro de Morais Filho**
✉️ j360074@hotmail.com

## 📄 Licença

[MIT](LICENSE) © 2026 Joaquim Pedro de Morais Filho
