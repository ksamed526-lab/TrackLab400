package com.tracklab400.app.data.updates

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateVersionsTest {

    @Test
    fun `parse accepts plain version`() {
        assertEquals(UpdateVersions.Version(0, 1, 0), UpdateVersions.parse("0.1.0"))
    }

    @Test
    fun `parse accepts v prefix`() {
        assertEquals(UpdateVersions.Version(1, 2, 3), UpdateVersions.parse("v1.2.3"))
    }

    @Test
    fun `parse rejects malformed input`() {
        assertNull(UpdateVersions.parse("abc"))
        assertNull(UpdateVersions.parse("1.2"))
        assertNull(UpdateVersions.parse(""))
    }

    @Test
    fun `isNewer true when remote patch is greater`() {
        assertTrue(UpdateVersions.isNewer("v0.1.1", "0.1.0"))
    }

    @Test
    fun `isNewer true when remote minor is greater`() {
        assertTrue(UpdateVersions.isNewer("v0.2.0", "0.1.9"))
    }

    @Test
    fun `isNewer true when remote major is greater`() {
        assertTrue(UpdateVersions.isNewer("v1.0.0", "0.9.9"))
    }

    @Test
    fun `isNewer false when versions are equal`() {
        assertFalse(UpdateVersions.isNewer("v0.1.0", "0.1.0"))
    }

    @Test
    fun `isNewer false when remote is older`() {
        assertFalse(UpdateVersions.isNewer("v0.0.9", "0.1.0"))
    }

    @Test
    fun `isNewer false when remote tag is malformed`() {
        assertFalse(UpdateVersions.isNewer("latest", "0.1.0"))
    }
}