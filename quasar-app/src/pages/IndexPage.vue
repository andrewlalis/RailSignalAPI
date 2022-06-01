<template>
  <q-page class="q-pa-md">
    <div class="row">
      <div class="col-md-6">
        <div class="text-h2">Welcome to Rail Signal</div>
        <hr>
        <p>
          To begin, expand the menu on the left, and select a rail system, or
          create a new one. The remainder of this page will serve as a quick
          guide on how to use Rail Signal to manage your rail system more
          effectively.
        </p>
      </div>
    </div>
    <index-page-section title="Introduction to Rail Networks">
      <q-img src="~assets/img/guide/layout.png"/>
      <p>
        The above diagram illustrates all of the basic concepts you need to
        know in order to build and manage your rail networks.
      </p>
      <p>
        Each rail system can be conceptually split up into lots of small
        <strong>segments</strong>, each of which represents a single part of
        the network that a single train should go through at once. For
        example, in our diagram, each shaded area is a segment. We only want
        one train to go through the junction at once, or there might be a
        crash!
      </p>
      <p>
        At the places where segments meet, we see a <strong style="color: #963ae0">segment boundary</strong>
        which is also denoted with a red dotted line for convenience. This is
        a physical point on a track where trains travel from one segment to
        another. What's special about segment boundaries is that they're where
        we can used devices to track trains moving in and out of segments. To
        put it simply, imagine there's a little computer next to each segment
        boundary point that sends a message saying, "Hey! A train just passed!"
        every time that it detects a train going over it.
      </p>
      <p>
        Now that we've covered segments and segment boundaries, we can now
        display a segment's status using a <strong>signal</strong>. A signal
        is a device that is linked to a segment, and whenever the segment's
        status updates (<em>when a train enters or leaves it</em>), the signal
        will be updated as well. Usually, signals are placed near the segment
        boundary, so that approaching trains know whether they're safe to
        continue, but with Rail Signal, you can place a signal anywhere, and
        connect it to any segment.
      </p>
      <p>
        Finally, unless you're just making a boring single-line loop, you'll
        most likely have some <strong style="color: #f5bc42">switches</strong>
        in your network. Switches are just sections of rail that allow trains
        to choose between two different paths to take. Rail Signal gives you
        the ability to manage these automatically, so you can use this web
        interface to configure switches instead of doing it manually.
      </p>
    </index-page-section>
    <index-page-section title="Paths and Path Nodes">
      <p>
        We mentioned segment boundaries and switches earlier, as simple
        components that you can add to your network in order to link it to the
        internet. There's more to it than that, however.
      </p>
      <p>
        Behind the scenes, your Rail Signal models your network as a set of
        <strong>path nodes</strong>, where each node can be connected to any
        other number of nodes. A train travels through your network by moving
        from node to node, until it reaches its desired destination. Both the
        segment boundary and switch are types of path nodes.
      </p>
      <ul>
        <li>
          Segment boundaries may only be connected to at most two nodes. This
          is because a segment boundary is fundamentally just a point on a
          single rail line.
        </li>
        <li>
          Switches are connected to nodes based on their set of defined
          configurations. In the example diagram, our switch allows two
          possible configurations:
          <ul>
            <li>Between the <strong style="color: #3cadab">blue</strong> and <strong style="color: #7169b4">purple</strong> segments.</li>
            <li>Between the <strong style="color: #81d07b">green</strong> and <strong style="color: #7169b4">purple</strong> segments.</li>
          </ul>
          This implies that our switch node is connected to three other nodes:
          each of the segment boundaries that it allows traffic between.
        </li>
      </ul>
    </index-page-section>
    <index-page-section title="Drivers">
      <p>
        While you can play around in this web app as long as you'd like, the
        main point is to connect to an external rail system. That's done through
        a <strong>driver</strong>, which is a dedicated piece of code that sends
        and receives messages from the Rail Signal server. Usually, driver
        software will be installed into physical components in your system, like
        signals and trackside detectors, and switch levers. It's the responsibility
        of driver software to tell Rail Signal when a train crosses a segment
        boundary, or when a switch updates, or anything else it should know
        about.
      </p>
    </index-page-section>
    <index-page-section title="Advanced Usage">
      <q-img src="~assets/img/guide/layout2.png"/>
      <p>
        The above diagram shows a more typical network arrangement for a large
        scale, two-way mainline. Here, we see that each side of the main line
        has its own segment, so that trains can travel past each other without
        issue. We make the entire junction a single segment, so that only one
        train can pass through at a time. More advanced setups might have
        separate segments for bypass lines to avoid traffic jams. Beside each
        entrance and exit to the junction, we've placed a signal. On the
        outbound segments, the signal will report the status of the outbound
        segment, while on the inbound segments, the signal will show the
        status of the junction segment.
      </p>
    </index-page-section>
  </q-page>
</template>

<script>
import IndexPageSection from "components/IndexPageSection.vue";
export default {
  name: "IndexPage",
  components: { IndexPageSection }
};
</script>

<style scoped>

</style>
