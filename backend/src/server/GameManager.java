package src.server;

import java.util.Random;

public class GameManager {
    private String[] words = {"JAVA", "PYTHON", "CPLUSPLUS", "JAVASCRIPT",
        "PIZZA","EINSTEIN", "MURALLA CHINA","TESLA",
        "GUELAGUETZA", 
        "MOLE", 
        "OAXACA", 
        "TAMALES"
};
    private String[] hints = {
        "\nLenguaje de programación de Oracle", 
        "\nLenguaje de programación con serpiente", 
        "\nLenguaje con '++' en su nombre", 
        "\nLenguaje usado en desarrollo web",
        "\nComida italiana muy popular", // PIZZA
        "\nFísico teórico famoso por la teoría de la relatividad", // EINSTEIN
        "\nMurallas gigantes que se encuentran en China", // muralla
        "\nInventor de la corriente alterna y famosa bombilla eléctrica", // TESLA
        //PREGUNTAS DE OAXACA 
        "\nGran festividad de Oaxaca celebrada en julio, conocida por sus danzas y música.",
        "\nPlatillo tradicional de Oaxaca, conocido por su salsa espesa.",
        "\nNombre del estado donde se encuentran muchas culturas indígenas, como los zapotecas y mixtecos.",
        "\nPlatillo tradicional de masa, generalmente relleno de carne, verduras o chile."

    };
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
