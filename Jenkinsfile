pipeline {

    agent any
    tools {
        jdk 'graalvm-latest'
        gradle 'gradle-latest'
    }
    options { timestamps() }

    environment {

        group = 'subsinfo'
        system = 'subsinfo'
        modulesList = 'api'

        artifactoryUri = "http://www.cbi.org:8082/artifactory/${env.BRANCH_NAME}"
        apphome = """/store/${group}/${system}"""
    }

    parameters {
        choice(name: 'stageType', choices: 'all\nbuild\ndeploy', description: '')
        string(name: 'buildNum', defaultValue: '', description: 'build number')
    }

    stages {
        stage('Prepare') {
            steps {
                script {

                    ports = ['api' : '8069']

                    switch (env.BRANCH_NAME) {
                        case 'master':
                            hosts = ['api' : ['subsinfo-dc1', 'subsinfo-dc2']]
                            break
                        default:
                            break
                    }

                    modules = modulesList.split(", ")

                    buildNumber = params.buildNum
                }
            }
        }

        stage('Compile') {
            when { expression { params.stageType ==~ /(build|all)/ } }
            steps { sh 'gradle clean distTar' }
        }

        stage('Load DB data for tests') {
            when {
                expression { params.stageType ==~ /(build|all)/ }
            }
            steps {
                script {
                    loadDataToDB("model")
                }
            }
        }

        stage('Test') {
            when {
                expression { params.stageType ==~ /(build|all)/ }
            }
            steps { sh 'gradle test' }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('Publish') {
            when { expression { params.stageType ==~ /(build|all)/ } }
            steps {
                script {
                    for (i = 0; i < modules.size(); i++) {
                        stage("Publish ${modules[i]}") {
                            publish(modules[i])
                        }
                    }
                }
            }
        }

        stage('Load DB data for DEV') {
            when {
                expression { env.BRANCH_NAME ==~ /(dev)/ }
                expression { params.stageType ==~ /(deploy|all)/ }
            }
            steps {
                script {
                    loadDataToDB(dbHost, apphome, "model")
                }
            }
        }

        stage('Deploy') {
            when { expression { params.stageType ==~ /(deploy|all)/ } }
            steps {
                script {

                    setBuildNumber()

                    for (i = 0; i < modules.size(); i++) {

                        def moduleHosts = hosts.get(modules[i])

                        for (j = 0; j < moduleHosts.size(); j++) {

                            stage("Deploy ${modules[i]} on ${moduleHosts[j]}") {
                                deploy(modules[i], moduleHosts[j])
                            }

                            stage("Waiting for ${modules[i]} to start") {
                                check(modules[i], moduleHosts[j], ports.get(modules[i]))
                            }
                        }
                    }
                }
            }
        }
    }

    post { always { notifyBuild() } }
}

def setBuildNumber() {
    if (params.stageType == 'all')
        buildNumber = env.BUILD_NUMBER
    else if (buildNumber.isEmpty())
        error('You must specify build number!!!')

    echo "Build number set to: ${buildNumber}"
}

def loadDataToDB(String module) {
    timeout(1) {
        sh """chmod a+x ${env.WORKSPACE}/${module}/loader/sql-loader.sh"""
        sh """${env.WORKSPACE}/${module}/loader/sql-loader.sh /store/infrastructure/cassandra/cassandra-server"""
    }
}

def publish(String module) {

    def uploadSpec = """{
                      "files": [
                        {
                          "pattern": "${module}/build/distributions/*.tgz",
                          "target": "${env.BRANCH_NAME}/org/cbi/${group}/${system}/${system}-${module}/${env.BUILD_NUMBER}/"
                        }
                     ]
                    }"""

    def server = Artifactory.server "XXX_artifactory"
    server.upload(uploadSpec)
}

def loadDataToDB(String hostname, String appHome, String module) {
    timeout(1) {
        sshagent(credentials: [hostname], ignoreMissing: true) {

            sh """ssh -o StrictHostKeyChecking=no devel@${hostname} hostname"""
            sh """ssh devel@${hostname} cat /etc/*-release | grep PRETTY_NAME"""

            sh """ssh devel@${hostname} mkdir -p ${appHome}/${module}"""
            sh """ssh devel@${hostname} "cd ${appHome}/${module} && rm -rf *" """
            sh """ssh devel@${hostname} mkdir -p ${appHome}/${module}/loader/sql"""
            sh """rsync -rauvv --progress ${WORKSPACE}/${module}/loader/ devel@${hostname}:${appHome}/${module}/loader/"""
            sh """ssh devel@${hostname} chmod a+x ${appHome}/${module}/loader/sql-loader.sh"""
            sh """ssh devel@${hostname} ${appHome}/${module}/loader/sql-loader.sh /store/infrastructure/cassandra/cassandra-server"""
        }
    }
}

def deploy(String module, String hostname) {

    sh """wget --proxy=off -O ${WORKSPACE}/${module}/${system}-${module}.tgz ${artifactoryUri}/org/cbi/${group}/${system}/${system}-${module}/${buildNumber}/${system}-${module}.tgz"""

    sshagent(credentials: [hostname], ignoreMissing: true) {

        sh """ssh -o StrictHostKeyChecking=no devel@${hostname} hostname"""
        sh """ssh devel@${hostname} cat /etc/*-release | grep PRETTY_NAME"""

        sh """ssh devel@${hostname} mkdir -p ${appHome}/${system}-${module}"""
        sh """ssh devel@${hostname} find ${appHome}/${system}-${module} -mindepth 1 -name logs -prune -o -exec rm -rf {} +"""

        sh """rsync -rauvv --remove-source-files --progress ${WORKSPACE}/${module}/${system}-${module}.tgz devel@${hostname}:${appHome}"""
        sh """ssh devel@${hostname} tar -xzvf ${appHome}/${system}-${module}.tgz -C ${appHome}"""

        sh """ssh devel@${hostname} "echo 'branch: ${env.BRANCH_NAME}, build: ${buildNumber}' > ${appHome}/${system}-${module}/buildInfo" """
        sh """ssh devel@${hostname} ${appHome}/${system}-${module}/service/install ${env.BRANCH_NAME}"""
    }
}

def check(String module, String hostname, String port) {
    sshagent(credentials: [hostname], ignoreMissing: true) {

        def serviceStatus = sh(returnStdout: true, script: """ssh devel@${hostname} systemctl is-active app-${system}-${module}""").trim()
        println("Check status: ${serviceStatus}")
        if (serviceStatus != "active") error("System not start on ${hostname}")

        timeout(2) {
            waitUntil {
                return sh(returnStdout: true, script: """ssh devel@${hostname} exit | telnet ${hostname} ${port} 2>&1 | grep "refused" | wc -l""").trim().equals("0")
            }
        }
    }
}

def notifyBuild() {

    def notifyColor = ['SUCCESS': '#00FF00', 'FAILURE': '#FF0000', 'UNSTABLE': '#FF0000']

    def subject = "Build status: ${currentBuild.currentResult} Job: ${env.JOB_NAME} #${env.BUILD_ID}"
    def commitinfo = sh(returnStdout: true, script: "git show --pretty=format:'The author was %an, %ar. Commit message: %s' | sed -n 1p").trim()

    def uploadSpec = """[
        {
            "title": "${subject}",
            "text": "${commitinfo}",
            "color": "${notifyColor.get(currentBuild.currentResult)}",
            "attachment_type": "default",
            "actions": [
                {
                    "text": "Console output",
                    "type": "button",
                    "url": "${env.BUILD_URL}console"
                },
                {
                    "text": "Test results",
                    "type": "button",
                    "url": "${env.BUILD_URL}testReport"
                }
            ]
        }
    ]"""

    slackSend attachments: uploadSpec
    emailext attachLog: true, subject: subject, body: commitinfo, to: 'admin_jenkins@cbi.org'
}