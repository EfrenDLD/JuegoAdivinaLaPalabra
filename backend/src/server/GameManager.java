package src.server;

import java.util.Random;

public class GameManager {
    private String[] words = {"JAVA", "PYTHON", "CPLUSPLUS", "JAVASCRIPT"};
    private String[] hints = {"\nLenguaje de programación de Oracle", "\nLenguaje de programación con serpiente", "\nLenguaje con '++' en su nombre", "\nLenguaje usado en desarrollo web"};
    private String secretWord;
    private String hint;

    public GameManager() {
        Random random = new Random();
        int index = random.nextInt(words.length);
        this.secretWord = words[index];
        this.hint = hints[index];
    }

    public String checkGuess(String guess) {
        if (guess.equalsIgnoreCase(secretWord)) {
            return "¡Correcto! La palabra era " + secretWord;
        } else {
            return "Incorrecto, intenta de nuevo.";
        }
    }

    public String getHint() {
        return "\n Pista: " + hint;
    }

    public String getSecretWord() {
        return secretWord;
    }
}
