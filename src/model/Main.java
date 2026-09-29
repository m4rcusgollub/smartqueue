package model;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        FilaPrioridade fila = new FilaPrioridade();

        boolean executando = true;

        while (executando) {

            System.out.println("\n=== SMART QUEUE ===");
            System.out.println("1 - Cadastrar consumidor");
            System.out.println("2 - Chamar próximo");
            System.out.println("3 - Ver quantidade na fila");
            System.out.println("4 - Sair");
            System.out.print("Escolha: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {

                case 1:

                    System.out.print("\nDigite seu nome: ");
                    String nome = scanner.nextLine();

                    System.out.println("\nEscolha sua prioridade:");
                    System.out.println("1 - Baixa");
                    System.out.println("2 - Normal");
                    System.out.println("3 - Prioridade");
                    System.out.println("4 - Urgente");
                    System.out.print("Opção: ");

                    int opcaoPrioridade = scanner.nextInt();
                    scanner.nextLine();

                    Prioridade prioridade;

                    switch (opcaoPrioridade) {

                        case 1:
                            prioridade = Prioridade.BAIXA;
                            break;

                        case 2:
                            prioridade = Prioridade.NORMAL;
                            break;

                        case 3:
                            prioridade = Prioridade.PRIORIDADE;
                            break;

                        case 4:
                            prioridade = Prioridade.URGENTE;
                            break;

                        default:
                            System.out.println("Prioridade inválida!");
                            continue;
                    }

                    Consumidor consumidor =
                            new Consumidor(nome, prioridade);

                    fila.adicionar(consumidor);

                    System.out.println("\nCadastro realizado!");
                    System.out.println("ID: " + consumidor.getId());
                    System.out.println("Nome: " + consumidor.getNome());
                    System.out.println(
                            "Prioridade: " + consumidor.getPrioridade()
                    );

                    break;

                case 2:

                    if (fila.estaVazia()) {
                        System.out.println("\nA fila está vazia!");
                        break;
                    }

                    Consumidor proximo = fila.proximo();

                    System.out.println("\n=== ATENDIMENTO ===");
                    System.out.println("ID: " + proximo.getId());
                    System.out.println("Nome: " + proximo.getNome());
                    System.out.println(
                            "Prioridade: " + proximo.getPrioridade()
                    );

                    break;

                case 3:

                    System.out.println(
                            "\nQuantidade na fila: " + fila.tamanho()
                    );

                    break;

                case 4:

                    System.out.println("\nEncerrando Smart Queue...");
                    executando = false;

                    break;

                default:

                    System.out.println("\nOpção inválida!");
            }
        }

        scanner.close();
    }
}