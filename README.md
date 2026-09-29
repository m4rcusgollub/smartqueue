<div align="center">

# Smart Queue

Sistema desktop de gerenciamento de filas de atendimento com prioridades, múltiplos guichês, painel de chamada e totem de autoatendimento.

<img src="https://img.shields.io/badge/Java_17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
<img src="https://img.shields.io/badge/Swing-1F6FB2?style=for-the-badge&logo=openjdk&logoColor=white">
<img src="https://img.shields.io/badge/Java_2D-5382A1?style=for-the-badge&logo=openjdk&logoColor=white">
<img src="https://img.shields.io/badge/Java_Sound_API-4A5568?style=for-the-badge&logo=openjdk&logoColor=white">
<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white">

</div>

---

## Visão geral

O **Smart Queue** é um sistema desktop de gerenciamento de filas de atendimento que substitui o controle manual por um fluxo digital de senhas e chamadas.

As senhas são organizadas automaticamente por nível de prioridade e distribuídas aos guichês disponíveis.

O projeto separa as regras de negócio da apresentação, permitindo que a lógica da fila seja utilizada independentemente da interface gráfica.

### Principais componentes

* **Núcleo:** `PriorityQueue` responsável pela organização da fila.
* **Serviço:** gerenciamento de fila, atendimentos, histórico e métricas.
* **Interface:** Java Swing + Java 2D.
* **Totem:** retirada de senha pelo usuário.
* **Painel:** exibição das chamadas em uma tela dedicada.
* **Atendente:** gerenciamento dos atendimentos nos guichês.

---

## Funcionalidades

* Ordenação automática por nível de prioridade
* Totem de autoatendimento
* Geração de senha
* Comprovante com código, horário e posição na fila
* Tempo estimado de espera
* Painel de chamada para TV ou segundo monitor
* Alerta visual e sonoro
* Seleção de guichê
* Cronômetro de atendimento
* Rechamada de senha
* Finalização de atendimento
* Registro de ausência
* Dashboard com métricas
* Tempo médio de espera
* Distribuição da fila por prioridade
* Histórico de atendimentos
* Cadastro manual de clientes
* Interface gráfica
* Modo terminal (CLI)
* Tema claro e escuro

---

## Níveis de prioridade

| Nível          | Critério                                                     |
| -------------- | ------------------------------------------------------------ |
| **Urgente**    | Emergências e casos graves                                   |
| **Prioridade** | Idosos (60+), gestantes, lactantes e pessoas com deficiência |
| **Normal**     | Atendimento geral                                            |
| **Baixa**      | Serviços informativos e triagens rápidas                     |

---

## Algoritmo de fila

A fila utiliza `PriorityQueue` juntamente com um `Comparator` para determinar a ordem de atendimento.

```java
private final Comparator<Consumidor> comparador = (a, b) ->
        b.getPrioridade().ordinal() - a.getPrioridade().ordinal();

private final Queue<Consumidor> fila =
        new PriorityQueue<>(comparador);
```

O enum `Prioridade` é declarado na seguinte ordem:

```text
BAIXA       → 0
NORMAL      → 1
PRIORIDADE  → 2
URGENTE     → 3
```

A comparação é feita em ordem decrescente, fazendo com que níveis de maior prioridade sejam posicionados antes dos níveis inferiores.

O projeto também possui uma forma de consultar a fila para exibição na interface sem remover os consumidores da fila original.

> **Observação:** a utilização de `ordinal()` foi adotada neste projeto para simplificar o exercício de `enum` e `Comparator`. Em uma implementação de produção, uma prioridade explícita poderia ser utilizada para evitar dependência da ordem de declaração do enum.

---

## Telas

### Totem de autoatendimento

Interface destinada a quiosques de recepção.

* Seleção de categoria
* Campo opcional para nome
* Geração de senha
* Número formatado, como `#001`
* Data e horário
* Posição na fila
* Tempo estimado de espera
* Fechamento automático do comprovante

### Painel de chamada

Interface destinada a TVs e monitores de sala de espera.

* Senha chamada em destaque
* Guichê de destino
* Últimas senhas chamadas
* Animação visual durante a chamada
* Alerta sonoro
* Relógio em tempo real
* Data
* Suporte a janela independente para segundo monitor ou projetor

### Console do atendente

Interface utilizada pelos operadores.

* Seleção do guichê
* Atendimento atual
* Cronômetro
* Chamar próxima senha
* Rechamar senha
* Finalizar atendimento
* Registrar ausência
* Visualização dos próximos consumidores

### Dashboard

Painel gerencial com:

* Total de pessoas na fila
* Tempo médio de espera
* Total de atendimentos
* Próximo consumidor
* Distribuição por prioridade
* Tabela da fila atual

### Fila e histórico

O sistema também disponibiliza:

* Cadastro manual de consumidores
* Visualização da fila
* Tempo individual de espera
* Histórico de atendimentos
* Guichê utilizado
* Data e horário
* Situação final do atendimento

---

## Arquitetura

```text
Consumidor / Entrada
        │
        ▼
   FilaService
        │
        ▼
 FilaPrioridade
        │
        ▼
PriorityQueue
        │
        ├───────────────┬────────────────┐
        ▼               ▼                ▼
     Totem          Painel TV        Atendimento
                                        │
                                        ▼
                                   Histórico
```

A aplicação mantém a lógica de negócio separada da camada visual, facilitando a manutenção e futuras evoluções do sistema.

---

## Estrutura do projeto

```text
fila/
├── src/
│   ├── app/
│   │   └── SmartQueue.java
│   │
│   ├── model/
│   │   ├── Consumidor.java
│   │   ├── Prioridade.java
│   │   ├── FilaPrioridade.java
│   │   └── Main.java
│   │
│   ├── service/
│   │   ├── Atendimento.java
│   │   └── FilaService.java
│   │
│   └── ui/
│       ├── Estilo.java
│       ├── Paleta.java
│       ├── Pintura.java
│       ├── Icone.java
│       ├── JanelaPrincipal.java
│       ├── JanelaPainelTV.java
│       └── visao/
│
├── ferramentas/
│   └── Previa.java
│
├── executaveis/
│   ├── SmartQueue.jar
│   └── SmartQueue.exe
│
├── pom.xml
└── README.md
```

---

## Tecnologias

| Área                          | Tecnologia     |
| ----------------------------- | -------------- |
| Linguagem                     | Java 17+       |
| Interface                     | Java Swing     |
| Renderização                  | Java 2D        |
| Áudio                         | Java Sound API |
| Build                         | Maven          |
| Gerenciamento de dependências | Maven          |

---

# Como executar

## Opção 1 — Executável Windows

Para usuários do Windows, **não é necessário compilar o projeto**.

O executável já está disponível no repositório:

```text
executaveis/SmartQueue.exe
```

Basta executar:

```text
SmartQueue.exe
```

> O executável foi gerado para facilitar a utilização do sistema sem necessidade de configurar o ambiente Java manualmente.

---

## Opção 2 — Arquivo JAR

Também está disponível uma versão `.jar`:

```text
executaveis/SmartQueue.jar
```

Para executar:

```bash
java -jar executaveis/SmartQueue.jar
```

É necessário possuir o **JDK/JRE 17 ou superior** instalado.

---

## Opção 3 — Executar pelo código-fonte

Para desenvolvimento, é possível executar diretamente o código-fonte.

### Compilação

Windows PowerShell:

```powershell
javac -encoding UTF-8 -d out/production/fila $(Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

Linux/macOS:

```bash
javac -encoding UTF-8 -d out/production/fila $(find src -name "*.java")
```

### Interface gráfica

```bash
java -cp out/production/fila app.SmartQueue
```

### Modo terminal

```bash
java -cp out/production/fila model.Main
```

---

## Maven

O projeto também possui configuração Maven.

Para compilar:

```bash
mvn compile
```

Para executar, caso o plugin de execução esteja configurado:

```bash
mvn exec:java
```

---

## Executáveis incluídos

O repositório já contém os arquivos compilados para facilitar os testes:

```text
executaveis/
├── SmartQueue.exe
└── SmartQueue.jar
```

Assim, quem acessar o projeto pode **testar a aplicação diretamente**, sem precisar primeiro configurar o ambiente de desenvolvimento.

---

## Objetivo do projeto

O Smart Queue foi desenvolvido como um projeto prático para aplicar conceitos de desenvolvimento Java, incluindo:

* Programação Orientada a Objetos
* Classes e objetos
* Encapsulamento
* Enum
* Collections
* Queue
* PriorityQueue
* Comparator
* Separação de responsabilidades
* Interfaces gráficas
* Gerenciamento de estado
* Manipulação de eventos
* Maven

O projeto também serve como base para futuras evoluções, como persistência em banco de dados, API REST e arquitetura cliente-servidor.

---

## Autor

**Marcus Gollub**

Projeto desenvolvido para prática e evolução em desenvolvimento Java.
