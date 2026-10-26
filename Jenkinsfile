// ==============================================================================
// PIPELINE CI/CD - vg-ms-paymentservice (FideiNexus)
// Cumplimiento:
// - Criterio 3: Ejecución y validación de pruebas unitarias (4 pts)
// - Criterio 4: Integración con SonarQube / SonarCloud y Quality Gate (4 pts)
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
        SONAR_ORG         = 'as241s5-prs3'
    }

    stages {
        // ─────────────────────────────────────────────────────────────────────
        // 1. STAGE: Descarga del Código Fuente
        // ─────────────────────────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "Descargando código fuente para ${env.SERVICE}..."
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(returnStdout: true, script: 'git rev-parse --short HEAD', encoding: 'UTF-8').trim()
                    env.GIT_AUTHOR       = sh(returnStdout: true, script: 'git log -1 --pretty=%an', encoding: 'UTF-8').trim()
                }
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
                        sh './mvnw clean compile -B'
                    } else {
                        bat 'mvnw.cmd clean compile -B'
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
        // 4. STAGE: Análisis de Calidad de Código SonarQube / SonarCloud (Criterio 4)
        // ─────────────────────────────────────────────────────────────────────
        stage('SonarQube Analysis') {
            steps {
                echo "Iniciando análisis de calidad y cobertura en SonarQube..."
                script {
                    // Se ejecuta dentro del entorno configurado de SonarQube
                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                        withSonarQubeEnv('SonarQube') {
                            if (isUnix()) {
                                sh './mvnw sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.organization=${SONAR_ORG} -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml -B'
                            } else {
                                bat "mvnw.cmd sonar:sonar -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} -Dsonar.organization=${env.SONAR_ORG} -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml -B"
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
                echo "Esperando verificación de Quality Gate de SonarQube..."
                script {
                    catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                        timeout(time: 2, unit: 'MINUTES') {
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
                        message: """✅ *BUILD EXITOSO:* `${env.SERVICE}`
*Build:* #${env.BUILD_NUMBER} | *Rama:* `${env.BRANCH_NAME ?: 'develop'}` | *Commit:* `${env.GIT_COMMIT_SHORT ?: 'HEAD'}`
*Autor:* ${env.GIT_AUTHOR ?: 'DevOps'}
*Pruebas Unitarias:* ${passedTests}/${totalTests} superadas (0 fallidas)
*Duración:* ${duration}
*Estado:* Quality Gate superado. Listo para despliegue."""
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
                        message: """❌ *BUILD FALLIDO:* `${env.SERVICE}`
*Build:* #${env.BUILD_NUMBER} | *Rama:* `${env.BRANCH_NAME ?: 'develop'}`
*Causa:* Pruebas unitarias fallidas o error en compilación.
*Revisar logs:* <${env.BUILD_URL}console|Consola de Jenkins>"""
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
                        message: """⚠️ *BUILD INESTABLE:* `${env.SERVICE}`
*Build:* #${env.BUILD_NUMBER} | *Rama:* `${env.BRANCH_NAME ?: 'develop'}`
*Alerta:* Quality Gate no cumplió con el umbral de cobertura o estándares."""
                    )
                }
            }
        }
    }
}
