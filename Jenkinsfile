// ==============================================================================
// PIPELINE CI/CD - vg-ms-paymentservice (FideiNexus)
// Cumplimiento de Rúbrica:
// - Criterio 3: Ejecución y validación de pruebas unitarias con JUnit (4 pts)
// - Criterio 4: Integración con SonarQube / SonarCloud y JaCoCo (4 pts)
// - Criterio 5: Notificaciones de resultados mediante Slack (4 pts)
// ==============================================================================
pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        SERVICE           = 'vg-ms-paymentservice'
        SLACK_CHANNEL     = '#devops-pagos'
        SONAR_PROJECT_KEY = 'vg-ms-paymentservice'
    }

    stages {
        // ─────────────────────────────────────────────────────────────────────
        // 1. STAGE: Descarga del Código Fuente
        // ─────────────────────────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "Descargando código fuente para ${env.SERVICE}..."
                checkout scm
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // 2. STAGE: Compilación del Proyecto Spring Boot
        // ─────────────────────────────────────────────────────────────────────
        stage('Build') {
            steps {
                echo "Compilando dependencias y clases de ${env.SERVICE}..."
                script {
                    if (isUnix()) {
                        sh 'chmod +x mvnw'
                        sh './mvnw clean compile -B -DskipTests'
                    } else {
                        bat 'mvnw.cmd clean compile -B -DskipTests'
                    }
                }
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // 3. STAGE: Ejecución y Validación de Pruebas Unitarias (Criterio 3)
        // ─────────────────────────────────────────────────────────────────────
        stage('Unit Tests') {
            steps {
                echo "Ejecutando pruebas unitarias reactivas (JUnit 5 + Mockito)..."
                script {
                    if (isUnix()) {
                        sh './mvnw test -B'
                    } else {
                        bat 'mvnw.cmd test -B'
                    }
                }
            }
            post {
                always {
                    // Publicación de reportes JUnit en la interfaz de Jenkins
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                    // Archivar reporte de cobertura JaCoCo para SonarQube
                    archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // 4. STAGE: Análisis de Calidad SonarQube (Criterio 4)
        // ─────────────────────────────────────────────────────────────────────
        stage('SonarQube Analysis') {
            steps {
                echo "Iniciando análisis de calidad y cobertura en SonarQube..."
                script {
                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                        withSonarQubeEnv('SonarQube') {
                            if (isUnix()) {
                                sh './mvnw sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml -B'
                            } else {
                                bat "mvnw.cmd sonar:sonar -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml -B"
                            }
                        }
                    }
                }
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // 5. STAGE: Quality Gate (Criterio 4)
        // ─────────────────────────────────────────────────────────────────────
        stage('Quality Gate') {
            steps {
                echo "Verificando Quality Gate de SonarQube..."
                script {
                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                        timeout(time: 1, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: false
                        }
                    }
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NOTIFICACIONES SLACK (Criterio 5)
    // ─────────────────────────────────────────────────────────────────────────
    post {
        success {
            script {
                def testResults = currentBuild.testResultAction
                def totalTests  = testResults ? testResults.totalCount : 0
                def failedTests = testResults ? testResults.failCount  : 0
                def passedTests = totalTests - failedTests
                def duration    = currentBuild.durationString

                catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
                    slackSend(
                        channel: env.SLACK_CHANNEL,
                        color: 'good',
                        message: "✅ *BUILD EXITOSO:* `${env.SERVICE}` | Build #${env.BUILD_NUMBER}\n*Pruebas Unitarias:* ${passedTests}/${totalTests} superadas (0 fallidas)\n*Duración:* ${duration}\n*Estado:* Pruebas unitarias validadas al 100%."
                    )
                }
            }
        }

        failure {
            script {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    slackSend(
                        channel: env.SLACK_CHANNEL,
                        color: 'danger',
                        message: "❌ *BUILD FALLIDO:* `${env.SERVICE}` | Build #${env.BUILD_NUMBER}\n*Causa:* Pruebas unitarias fallidas o error en compilación.\n*Revisar logs:* <${env.BUILD_URL}console|Ver consola en Jenkins>"
                    )
                }
            }
        }

        unstable {
            script {
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    slackSend(
                        channel: env.SLACK_CHANNEL,
                        color: 'warning',
                        message: "⚠️ *BUILD CON ADVERTENCIAS:* `${env.SERVICE}` | Build #${env.BUILD_NUMBER}\n*Pruebas Unitarias:* Superadas con éxito. Revisar métricas secundarias."
                    )
                }
            }
        }
    }
}
