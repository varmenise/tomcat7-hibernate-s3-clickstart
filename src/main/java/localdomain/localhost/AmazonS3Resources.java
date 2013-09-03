/*
 * Copyright 2010-2013, the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package localdomain.localhost;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Logger;

public class AmazonS3Resources {

    protected final Logger logger = Logger.getLogger(getClass().getName());
    private final Random random = new Random();
    private String bucketName;
    private AmazonS3Client amazonS3Client;

    /**
     * @param accessKey
     * @param secretKey
     * @param bucketName
     * @throws AmazonServiceException
     */
    public AmazonS3Resources(String accessKey, String secretKey, String bucketName) throws AmazonServiceException {
        AWSCredentials credentials;

        credentials = new BasicAWSCredentials(accessKey, secretKey);
        amazonS3Client = new AmazonS3Client(credentials);
        if (!amazonS3Client.doesBucketExist(bucketName)) {
            throw new IllegalArgumentException("Bucket " + bucketName + " does not exist");
        }
        this.bucketName = bucketName;
    }

    /**
     * Upload an image to Amazon S3
     *
     * @param in
     * @param objectMetadata
     * @param fileName
     * @return uploaded image URL
     * @throws AmazonServiceException
     * @throws IllegalArgumentException
     */
    @Nonnull
    public String uploadImage(@Nonnull InputStream in, @Nonnull ObjectMetadata objectMetadata, @Nonnull String fileName) throws AmazonServiceException, IllegalArgumentException {

        int idx = fileName.lastIndexOf(".");
        if (idx == -1)
            throw new IllegalArgumentException("Invalid filename without extension: " + fileName);

        String uploadedFileExtension = fileName.substring(idx + 1, fileName.length());

        HashSet<String> permittedFileExtensions = new HashSet<String>();
        permittedFileExtensions.add("jpg");
        permittedFileExtensions.add("png");
        permittedFileExtensions.add("gif");

        uploadedFileExtension = uploadedFileExtension.toLowerCase();

        if (!permittedFileExtensions.contains(uploadedFileExtension))
            throw new IllegalArgumentException("Invalid file extension '" + uploadedFileExtension + "' in " + fileName);

        Long randomImageName = Math.abs(random.nextLong());
        String randomImageNameWithFileExtension = randomImageName + "." + uploadedFileExtension;

        amazonS3Client.putObject(bucketName, randomImageNameWithFileExtension, in, objectMetadata);

        String imageUrl = "https://s3.amazonaws.com/" + bucketName + "/" + randomImageNameWithFileExtension;
        logger.info("Image uploaded to Amazon S3 " + imageUrl);

        return imageUrl;
    }
}
