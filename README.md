# Desafio API de Controle Financeiro - XPTO

![Java](https://img.shields.io/badge/Java-8-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.x-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)
![Status](https://img.shields.io/badge/Status-Concluído-success)

## 1. Descrição do Desafio

Este projeto é uma API REST desenvolvida como solução para o **Desafio da Célula Financeiro e Controladoria**. [cite_start]O objetivo é criar um sistema para o controle de receitas e despesas dos clientes (PF e PJ) da empresa fictícia XPTO[cite: 3, 5].

[cite_start]A aplicação gerencia o cadastro de clientes, suas contas e endereços, e processa as movimentações financeiras (receitas e despesas)[cite: 5, 7]. [cite_start]O principal diferencial do sistema é a lógica de negócio para o cálculo da receita da própria XPTO, que é baseada na quantidade de transações que cada cliente realiza[cite: 6, 25].

[cite_start]O desafio originalmente requisitava o uso do banco de dados Oracle [cite: 3][cite_start], mas para este projeto foi utilizado o **MySQL 8.0** (mantendo a conformidade com o requisito de uso de um objeto PL/SQL ).

---

## 2. Funcionalidades Principais

* [cite_start]**CRUD de Clientes**: Sistema completo para manter clientes, sejam eles Pessoa Física (PF) ou Pessoa Jurídica (PJ)[cite: 7, 31].
* [cite_start]**CRUD de Endereços e Contas**: Gerenciamento de múltiplos endereços e contas bancárias por cliente[cite: 17, 33, 35].
* [cite_start]**Cadastro com Movimentação Inicial**: Um cliente só pode ser cadastrado se fornecer uma movimentação inicial (saldo inicial).
* [cite_start]**Simulação de Integração**: Um endpoint (`POST /api/movimentacoes`) simula o recebimento de transações de instituições financeiras[cite: 18, 24].
* [cite_start]**Lógica de Receita XPTO**: Cálculo automático da taxa (receita) que a XPTO ganha em cima de cada movimentação do cliente[cite: 25].
* [cite_start]**Regras de Manutenção de Contas**:
    * [cite_start]Não permite alteração de uma conta se ela já possui movimentações.
    * [cite_start]Permite apenas a exclusão lógica da conta (inativação).
* [cite_start]**Relatórios Detalhados**: Geração de 4 relatórios complexos para análise de saldo e receita[cite: 37, 45, 55, 57].
* [cite_start]**Integração com Banco de Dados**: Utilização de uma `Function` nativa do MySQL, chamada pelo Java[cite: 64, 65].

---

## 3. Tecnologias Utilizadas

* **Java 8** 
* **Spring Boot** 
* **MySQL 8.0**
* **JPA / Hibernate**
* **Maven**
* **JUnit 5 & Mockito**
* **Lombok**

---

## 4. Padrões de Projeto e Boas Práticas

[cite_start]Conforme solicitado[cite: 70], este projeto aplicou diversas boas práticas e padrões de arquitetura:

* **Arquitetura em Camadas (Layered Architecture)**: O projeto é claramente dividido em:
    * `Controller` (Camada de API/Apresentação): Responsável por expor os endpoints REST.
    * `Service` (Camada de Negócio): Onde toda a lógica de negócio (cálculo de taxas, validações de relatórios) reside.
    * `Repository` (Camada de Persistência): Abstrai o acesso aos dados usando Spring Data JPA.
* **Padrão DTO (Data Transfer Object)**: Utilizamos DTOs (`ClienteRequestDTO`, `RelatorioSaldoClienteDTO`, etc.) para desacoplar a API das entidades de banco de dados. Isso evita a exposição de dados internos e previne problemas de serialização.
* **Injeção de Dependência (DI)**: Utilizada extensivamente pelo Spring para gerenciar o ciclo de vida dos componentes (`@Autowired`, `@Service`).
* **Separação de Responsabilidades (SoC)**: A lógica de negócio está isolada nos serviços. [cite_start]Por exemplo, o `MovimentacaoService` é o único responsável por saber como calcular a taxa da XPTO[cite: 25].
* [cite_start]**Tratamento de Exceções**: Criamos exceções customizadas (`ResourceNotFoundException`, `BusinessException`) para lidar com erros de negócio (ex: "Conta não pode ser alterada") e erros de busca (ex: "Cliente não encontrado")[cite: 69].
* [cite_start]**Testes Focados na Lógica de Negócio**: A classe de teste (`MovimentacaoServiceTest`) foi criada para validar especificamente a regra de negócio mais crítica: o cálculo das taxas da XPTO [cite: 27-29, 66].

---

## 5. Lógica de Negócio Principal: Receita XPTO

[cite_start]O núcleo do desafio é o cálculo da receita que a XPTO ganha sobre as operações dos clientes[cite: 25]. [cite_start]A regra é baseada em "janelas" de 30 dias, contadas a partir da `dataCadastro` do cliente[cite: 26].

A cada movimentação registrada (via `POST /api/movimentacoes`), o `MovimentacaoService` executa a seguinte lógica:
1.  [cite_start]Identifica a "janela" de 30 dias em que a transação está ocorrendo (ex: 0-29 dias, 30-59 dias, etc.)[cite: 26].
2.  Conta quantas movimentações o cliente já fez *dentro* dessa janela.
3.  [cite_start]Aplica a taxa correta sobre a nova movimentação, com base nas faixas [cite: 27-29]:
    * [cite_start]**Até 10 movimentações** na janela: **R$ 1,00** por movimentação[cite: 27].
    * [cite_start]**De 10 a 20 movimentações**: **R$ 0,75** por movimentação[cite: 28].
    * [cite_start]**Acima de 20 movimentações**: **R$ 0,50** por movimentação[cite: 29].

[cite_start]O valor dessa taxa é armazenado na própria tabela `Movimentacao` (coluna `valorTaxaXpto`) e serve de base para o Relatório de Receita da Empresa.

---

## 6. Requisito Obrigatório: PL/SQL

[cite_start]Para cumprir o requisito obrigatório de integração com um objeto de banco de dados, foi criada a seguinte **Function** no MySQL (equivalente a uma Function PL/SQL):

**`FN_CALCULAR_SALDO_ATUAL_CLIENTE(p_cliente_id BIGINT)`**

Esta função calcula o saldo total de um cliente somando todos os seus `CREDITOS` e subtraindo todos os seus `DEBITOS`.

[cite_start]Ela é chamada diretamente pelo Java através do `ClienteRepository`, usando uma Query Nativa (JPA), para alimentar o "Relatório de Saldo de Todos os Clientes"[cite: 55, 65].

```java
// Dentro de ClienteRepository.java
@Query(value = "SELECT FN_CALCULAR_SALDO_ATUAL_CLIENTE(:clienteId)", nativeQuery = true)
BigDecimal getSaldoAtualCliente(@Param("clienteId") Long clienteId);
```
## 7. Como Executar o Projeto
- **Java 8 (JDK 1.8)**
- **Maven 3.6+**
- **MySQL 8.0+**

## 1. Configuração do Banco de Dados
```SQL
CREATE DATABASE xpto_db;
```

## 2. Configuração do Banco de Dados
Antes de iniciar a aplicação, execute o script SQL abaixo no seu banco xpto_db para criar a função nativa:
```SQL
DELIMITER $$
CREATE FUNCTION `FN_CALCULAR_SALDO_ATUAL_CLIENTE`(
    p_cliente_id BIGINT
)
RETURNS DECIMAL(19,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_saldo DECIMAL(19,2) DEFAULT 0.00;
    
    SELECT 
        SUM(
            CASE 
                WHEN tipo = 'CREDITO' THEN valor 
                ELSE -valor 
            END
        )
    INTO v_saldo
    FROM movimentacao
    WHERE cliente_id = p_cliente_id;
    
    RETURN IFNULL(v_saldo, 0.00);
END$$
DELIMITER ;
```

## 3. Configuração da Aplicação
Abra o arquivo ```src/main/resources/application.properties``` e atualize as credenciais do seu banco MySQL:
```Properties
spring.datasource.url=jdbc:mysql://localhost:3306/xpto_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=sua_senha_aqui
```

## 4. Executando
Compile e execute a aplicação usando o Maven:
```Bash
mvn clean install
mvn spring-boot:run
```
A API estará disponível em ```http://localhost:8080```.


