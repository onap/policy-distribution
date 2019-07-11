FROM onap/policy-common-alpine:1.5.0

LABEL maintainer="Policy Team"

ARG POLICY_LOGS=/var/log/onap/policy/distribution

ENV POLICY_LOGS=$POLICY_LOGS
ENV POLICY_HOME=$POLICY_HOME/distribution

RUN mkdir -p $POLICY_HOME $POLICY_LOGS $POLICY_HOME/bin && \
    chown -R policy:policy $POLICY_HOME $POLICY_LOGS

RUN mkdir /packages
COPY /maven/* /packages
RUN tar xvfz /packages/policy-distribution.tar.gz --directory $POLICY_HOME \
     && rm /packages/policy-distribution.tar.gz

WORKDIR $POLICY_HOME
COPY policy-dist.sh  bin/.
RUN chown -R policy:policy * && chmod 755 bin/*.sh

USER policy
WORKDIR $POLICY_HOME/bin
ENTRYPOINT [ "bash", "./policy-dist.sh" ]
