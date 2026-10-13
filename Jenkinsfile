pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Unit Tests') {
            steps {
                echo 'Compilando el microservicio vg-ms-paymentService...'
                script {
                    if (isUnix()) {
                        sh 'chmod +x mvnw'
                        sh './mvnw clean test'
                    } else {
                        bat 'mvnw.cmd clean test'
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
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
