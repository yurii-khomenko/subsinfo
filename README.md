# subsinfo
MVP of Subscriber Information System

Results:
- 24000 HTTP requests per second per node(current 48000 by 2 nodes);
- same throughput on update.
- easy to vertical scaling(async server model);
- easy to horizontal scaling(Cassandra cluster);
- REST interface for clients;
- responsive, resilient, elastic and message driven solution.

This service deployed on 2VMs(4cores Xeon e5 2699 per VM, 8GB RAM, 200GB HDD) and has 1 application and 1 DB per node. It stores 150 000 000 profiles in Cassandra and consumes 6,9 GB of disk space. The service handle 48000 HTTP requests per second. It easy to vertical scaling because it uses fully asynchronous model of computing. It easy to horizontal scaling because builds as independent nodes around Cassandra DB. When I design that service I was inspired The Reactive Manifesto.
