🎨 SilenciosDrawings - Aplicativo de Desenho Digital em Kotlin 🎨

Aplicativo de criação artística com ferramentas intuitivas e paleta de cores personalizável.

-------------------------------------------------------------------------------------------------

✨ **Sobre o Projeto**

Aplicativo Android para desenho livre desenvolvido em Kotlin com:

✅ Canvas digital com múltiplas ferramentas (pincel, borracha, paleta)  
🎨 Sistema de camadas e opções de espessura/transparência  
📱 Interface moderna com Jetpack Compose  
📊 Arquitetura MVVM com ViewModel e StateFlow  

-------------------------------------------------------------------------------------------------

🚀 **Roadmap**

| Versão       | Status          | Observação                          |
|--------------|-----------------|-------------------------------------|
| Kotlin 1.9   | ✅ Estável      | Compatível com Android 8.0+ (API 26)|
| Jetpack Compose | ✅ Implementado | UI 100% declarativa                 |
| Exportar PNG | 🔄 Em desenvolvimento | Salvamento na galeria           |

-------------------------------------------------------------------------------------------------

🛠️ **Tecnologias & Ferramentas**

| Componente          | Detalhes                                   |
|---------------------|-------------------------------------------|
| Linguagem           | Kotlin 1.9                                |
| IDE                 | Android Studio Giraffe (2023.3.1)         |
| Arquitetura         | MVVM + Clean Architecture                 |
| Bibliotecas         | Jetpack Compose, ViewModel, Coroutines    |
| Dependências        | Material3, AndroidX Core                  |

-------------------------------------------------------------------------------------------------

📂 **Estrutura do Projeto**

SilenciosDrawings/

├── app/

│ ├── src/main/

│ │ ├── java/com/example/silenciosdrawings/

│ │ │ ├── components/ # ColorPalette, ToolsBar, ColorPickerDialog

│ │ │ ├── models/ # DrawingState

│ │ │ ├── ui/theme/ # Color, Theme, Type

│ │ │ ├── utils/ # ImageUtils

│ │ │ ├── viewmodels/ # DrawingViewModel

│ │ │ ├── views/ # DrawingScreen

│ │ │ └── MainActivity.kt # Ponto de entrada

│ │ └── res/ # Ícones, cores e recursos visuais

├── build.gradle.kts # Configurações principais

└── settings.gradle.kts # Configurações do projeto

-------------------------------------------------------------------------------------------------
⚡ **Como Executar**

**Pré-requisitos:**
- Android Studio Giraffe 2023.3+
- SDK Android 34+
- Dispositivo/emulador com API 26+

**Passos:**
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-user/SilenciosDrawings.git
   2.  Abra no Android Studio e sincronize o Gradle

    3. Execute em um emulador ou dispositivo físico
   -------------------------------------------------------------------------------------------------
   🔒 Boas Práticas Implementadas

✔️ Arquitetura limpa com separação de concerns
✔️ Estado gerenciado por ViewModel + Compose
✔️ Componentes reutilizáveis e testáveis
✔️ Documentação no código (KDoc)

🌟 Próximos Passos

    Adicionar ferramenta de preenchimento (balde)

    Implementar undo/redo com histórico

    Suporte a tablets e modo paisagem
    -------------------------------------------------------------------------------------------------
    📌 Compatibilidade
Mínima	Recomendada	Dependências Principais
Android 8.0	Android 13+	androidx.compose.material3:1.2.1
(API 26)	(API 33)	androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0
-------------------------------------------------------------------------------------------------
👨‍💻 Desenvolvido com ❤️ e 🎨 por você!
Apoio técnico dos parceiros DeepSeek e Claude! 🚀
