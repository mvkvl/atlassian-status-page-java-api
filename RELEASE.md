# Deployment procedure

1. Configure Sonatype servers in `~/.m2/settings.xml`

```
<settings>
  <servers>
    <server>
      <id>sonatype-nexus-snapshots</id>
      <username>{sonatype-jira-login}</username>
      <password>{sonatype-jira-password}</password>
    </server>
    <server>
      <id>sonatype-nexus-staging</id>
      <username>{sonatype-jira-login}</username>
      <password>{sonatype-jira-password}</password>
    </server>
  </servers>
</settings>
```

2. Check maven settings
```
mvn help:effective-settings
```

3. Sign artifacts during build

```
mvn -DskipTests -Dgpg.passphrase="${GPG_PASSPHRASE}" clean package
```

4. Deploy to Sonatype

```
mvn -DskipTests deploy
```

5. Open [Sonatype Nexus OSS](https://oss.sonatype.org)

6. Release artifact as described [here](https://blog.10pines.com/2018/06/25/publish-artifacts-on-maven-central/) at step 6
