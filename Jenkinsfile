pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Descarga el código desde tu repositorio de GitHub
                checkout scm
            }
        }

        stage('Build & Unit Tests') {
            steps {
                // Compila el proyecto Java Spring Boot y ejecuta las pruebas
                echo 'Compilando el microservicio vg-ms-paymentService...'
                if (isUnix()) {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean test'
                } else {
                    bat 'mvnw.cmd clean test'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Aquí agregaremos el comando de SonarQube en el siguiente paso de tu tarea
                echo 'Análisis de SonarQube pendiente de configurar...'
            }
        }
    }

    post {
        success {
            slackSend(channel: '#devops-pagos', 
                      color: 'good', 
                      message: "✅ SUCCESS: El pipeline de vg-ms-paymentService se ejecutó correctamente. Build #${env.BUILD_NUMBER}")
        }
        failure {
            slackSend(channel: '#devops-pagos', 
                      color: 'danger', 
                      message: "❌ FAILED: Error en el pipeline de vg-ms-paymentService. Revisar logs. Build #${env.BUILD_NUMBER}")
        }
    }
}