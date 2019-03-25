FROM onap/policy-common-alpine:1.4.0

LABEL maintainer="Policy Team"

ARG POLICY_LOGS=/var/log/onap/policy/distribution

ENV POLICY_LOGS ${POLICY_LOGS}
ENV POLICY_DISTRIBUTION_HOME=${POLICY_HOME}/distribution

RUN mkdir -p ${POLICY_DISTRIBUTION_HOME} ${POLICY_LOGS} ${POLICY_DISTRIBUTION_HOME}/bin && \
    chown -R policy:policy ${POLICY_DISTRIBUTION_HOME} ${POLICY_LOGS}

RUN mkdir /packages
COPY /maven/* /packages
RUN tar xvfz /packages/policy-distribution.tar.gz --directory ${POLICY_DISTRIBUTION_HOME} \
     && rm /packages/policy-distribution.tar.gz

WORKDIR ${POLICY_DISTRIBUTION_HOME}
COPY policy-dist.sh  bin/.
RUN chown -R policy:policy * && chmod +x bin/*.sh \
   && cp ${POLICY_DISTRIBUTION_HOME}/etc/ssl/* ${POLICY_HOME}/etc/ssl \
   && chown policy:policy ${POLICY_HOME}/etc/ssl/*

USER policy
WORKDIR ${POLICY_DISTRIBUTION_HOME}/bin
ENTRYPOINT [ "bash", "./policy-dist.sh" ]
