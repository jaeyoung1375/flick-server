pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare Secrets') {
            steps {
                withCredentials([file(credentialsId: 'motive-application-secret', variable: 'SECRET_FILE')]) {
                    sh '''
                           rm -f src/main/resources/application-secret.yml
                           cp $SECRET_FILE src/main/resources/application-secret.yml
                       '''
                }
            }
        }


        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test || true'
            }
            post {
                always {
                    junit testResults: '**/build/test-results/test/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    set -e
                    JAR_PATH="build/libs/motive-server-0.0.1-SNAPSHOT.jar"

                    if [ ! -f "$JAR_PATH" ]; then
                        echo "빌드 결과물을 찾을 수 없습니다: $JAR_PATH"
                        ls -la build/libs/
                        exit 1
                    fi

                    cp "$JAR_PATH" /app/motive-app/motive-server-0.0.1-SNAPSHOT.jar
                    sudo /bin/systemctl restart motive
                    sleep 3
                    sudo /bin/systemctl is-active motive
                '''
            }
        }
    }

    post {
        success {
            echo 'Build & Deploy succeeded'
        }
        failure {
            echo 'Build or Deploy failed'
        }
    }
}