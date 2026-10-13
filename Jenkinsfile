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
                echo 'Ejecutando el análisis de SonarQube...'
                withSonarQubeEnv('sonar-server') {
                    script {
                        if (isUnix()) {
                            sh './mvnw org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=vg-ms-paymentServic -Dsonar.projectName=vg-ms-paymentServic -Dsonar.token=$SONAR_AUTH_TOKEN'
                        } else {
                            bat 'mvnw.cmd org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=vg-ms-paymentServic -Dsonar.projectName=vg-ms-paymentServic -Dsonar.token=%SONAR_AUTH_TOKEN%'
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            slackSend(channel: '#devops-pagos', 
                      color: 'good', 
                      message: "✅ SUCCESS: El pipeline de vg-ms-paymentService se ejecutó correctamente con análisis de SonarQube. Build #${env.BUILD_NUMBER}")
        }
        failure {
            slackSend(channel: '#devops-pagos', 
                      color: 'danger', 
                      message: "❌ FAILED: Error en el pipeline de vg-ms-paymentService. Revisar logs. Build #${env.BUILD_NUMBER}")
        }
    }
}
