## Read me first

This project is licensed under both LGPLv3 and ASL 2.0. See file LICENSE for more details.

**Note the "L" in "LGPL". LGPL AND GPL ARE QUITE DIFFERENT!**

## What this is

This is an implementation of a Java 7
[`FileSystem`](http://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html) over FTP.

Strangely enough, even though Java 8 is out, very few people use the new file API provided by Java
7, even though it is vastly superior to `File`.

This project aims at showing what `FileSystem` can do for you by implemeting it over FTP. Sample
code:

```java
    /*
     * PLEASE DON'T ABUSE THAT... ftp.lip6.fr is but one FTP server; try and use another one
     * if you can!
     */
    public static void main(final String... args)
        throws IOException
    {
        final FtpAgentFactory agentFactory = new CommonsNetFtpAgentFactory();
        final FtpFileSystemProvider provider
            = new FtpFileSystemProvider(agentFactory);
        final URI uri = URI.create("ftp://ftp.lip6.fr");
        final Map<String, String> map = Collections.emptyMap();

        try (
            final FileSystem fs = provider.newFileSystem(uri, map);
        ) {
            final Path path = fs.getPath("/");
            for (final Path entry : Files.newDirectoryStream(path))
                System.out.println(entry);
            final Path lsLrgz = path.resolve("ls-lR.gz");
            final Path dst = Paths.get("/tmp/foo.gz");
            Files.copy(lsLrgz, dst);
        }
    }
```

## Status

So, OK, it works, kind of. But you should consider it **alpha quality**.

Here is what is NOT implemented:

* symlink support;
* write access;
* subpath support (ie, accessing `ftp://foo.bar/somewhere/else/than/root`);
* `SeekableByteChannel` support (although that probably makes no sense for FTP at all);
* choice of passive versus active for data channels.

Also, the code lacks documentation and tests.

## Current architecture

OK, so the first problem is with the FTP protocol itself; fundamentally, it is not multithreaded.
One command channel, only one possible data channel at a time.

The code therefore maitains a **bounded queue** of FTP clients (5; at the moment it is not
configurable). Each time a new data channel is needed, the `FileSystemProvider` (since it is the
only class doing real I/O) takes a client from the queue. If all clients are currently busy, the
provider blocks until one is available.

In the event of a data transfer, the client is unavailable to the pool until the data transfer has
completed; so, if you download a big file over a quite slow connection, you'll have one client less
to play with...

The interface (`FtpAgent`) for one FTP client currently has one implementation, over Apache's
commons-net (3.3). I haven't tried other FTP client libraries, but if you want to play with this,
you can.

## Contributing

In any way you can!

I am no expert in FTP, I have used components which I know how they work, and this is my first try
at implementing a custom `FileSystem`.

But this API shows promises. FTP is but one thing you can "abstract" through this API, more is to
come...

